package de.tum.imomesa.workbench.controllers.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.controllers.AbstractExplorerController;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class ExplorerController extends AbstractExplorerController implements EventHandler {

	@FXML
	private BorderPane borderPane;

	@FXML
	private ToolBar topToolBar;
	@FXML
	private ToolBar leftToolBar;

	@FXML
	private TextField searchTextField;

	@FXML
	private ToggleButton ports;
	@FXML
	private ToggleButton properties;
	@FXML
	private ToggleButton scenarios;
	@FXML
	private ToggleButton monitors;
	@FXML
	private ToggleButton components;
	@FXML
	private ToggleButton behaviors;

	@FXML
	private TreeView<Element> treeView;

	private TreeItem<Element> root;
	private TreeItem<Element> scenarioItem;
	private TreeItem<Element> componentItem;
	private TreeItem<Element> selectedItem;

	private Simulator simulator;

	@FXML
	public void initialize() {
		EventBus.getInstance().subscribe(this);

		borderPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				borderPane.setLeft(null);
				borderPane.setBottom(null);

				if (newValue.intValue() > 300) {
					borderPane.setLeft(leftToolBar);
					borderPane.setBottom(null);
					leftToolBar.setOrientation(Orientation.VERTICAL);
				} else {
					borderPane.setLeft(null);
					borderPane.setBottom(leftToolBar);
					leftToolBar.setOrientation(Orientation.HORIZONTAL);
				}
			}
		});

		searchTextField.prefWidthProperty().bind(topToolBar.widthProperty().subtract(16));
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public void setElement(DefinitionComponent c) {
		componentItem = getTreeItem(c);
		componentItem.setExpanded(true);

		// create root overview element
		root = new TreeItem<Element>(new OverviewElement<>(null, Component.class, "Simulation"),
				ImageHelper.getImageAsIcon("icons/simulation.png"));
		root.getChildren().add(componentItem);
		root.setExpanded(true);

		// set on selection
		treeView.setRoot(root);
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Element>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Element>> arg0, TreeItem<Element> oldItem,
					TreeItem<Element> newItem) {
				// Check null
				if (newItem == null) {
					return;
				}

				if (newItem != selectedItem) {
					selectedItem = newItem;

					List<Element> extendedContext = new ArrayList<>();
					List<Element> context = new ArrayList<>();

					Element element;

					// Default for root node
					if (newItem.equals(root)) {
						extendedContext.add(simulator.getComponent());
						context.add(simulator.getComponent());

						element = simulator.getComponent();
					} else {
						// Build context
						TreeItem<Element> currentItem = newItem;

						while (currentItem != root) {
							if (currentItem.getValue() != null && !(currentItem.getValue() instanceof OverviewElement)
									&& !extendedContext.contains(currentItem.getValue())) {
								extendedContext.add(0, currentItem.getValue());
								if (currentItem.getValue() instanceof Component) {
									context.add(0, currentItem.getValue());
								}
							}
							currentItem = currentItem.getParent();
						}

						// Find component
						element = newItem.getValue();
					}

					Component<?> component = null;
					if (context.size() > 0) {
						component = (Component<?>) context.get(context.size() - 1);
					}

					// Publish event
					EventBus.getInstance()
							.publish(new SimulationSelectionEvent(extendedContext, element, context, component));
				}
			}
		});
		treeView.getSelectionModel().select(scenarioItem);
	}

	public void handle(SimulationStepEvent event) {
		// get memory and step
		Memory memory = simulator.getMemory();
		// get proxies
		Set<ReferenceComponent> proxies = memory.getProxy(event.getStep());

		// check which items to remove
		List<TreeItem<Element>> removeList = new ArrayList<>();

		for (TreeItem<Element> item : root.getChildren()) {
			if (item.getValue() instanceof ReferenceComponent && !proxies.contains(item.getValue())) {
				removeList.add(item);
			}
		}

		// check which items to add
		List<ReferenceComponent> addList = new ArrayList<>();

		for (ReferenceComponent ref : proxies) {
			boolean found = false;
			for (TreeItem<Element> item : root.getChildren()) {
				if (item.getValue().equals(ref)) {
					found = true;
					break;
				}
			}
			if (!found) {
				addList.add(ref);
			}
		}

		// remove the children
		TreeItem<Element> selected = treeView.getSelectionModel().getSelectedItem();

		treeView.getSelectionModel().clearSelection();

		root.getChildren().removeAll(removeList);

		// add all not yet handled children
		for (ReferenceComponent proxy : addList) {
			root.getChildren().add(getTreeItem(proxy));
		}

		if (!removeList.contains(selected)) {
			treeView.getSelectionModel().select(selected);
		} else {
			treeView.getSelectionModel().selectFirst();
		}
	}

	private TreeItem<Element> getTreeItem(Component<?> component) {
		TreeItem<Element> rootItem = new TreeItem<Element>(component, ImageHelper.getIcon(component.getClass()));

		if (component instanceof DefinitionComponent) {
			DefinitionComponent def = (DefinitionComponent) component;

			if (!def.getPorts().isEmpty()) {
				TreeItem<Element> portsItem = new TreeItem<>(new OverviewElement<>(def, DefinitionPort.class, "Ports"),
						ImageHelper.getFolderIcon(Port.class));
				for (DefinitionPort port : def.getPorts()) {
					portsItem.getChildren().add(getTreeItem(port));
				}
				rootItem.getChildren().add(portsItem);
			}

			if (!def.getProperties().isEmpty()) {
				TreeItem<Element> propsItem = new TreeItem<>(new OverviewElement<>(def, Property.class, "Properties"),
						ImageHelper.getFolderIcon(Property.class));
				for (Property property : def.getProperties()) {
					propsItem.getChildren().add(getTreeItem(property));
				}
				rootItem.getChildren().add(propsItem);
			}

			if (!def.getScenarios().isEmpty() && def.equals(simulator.getComponent())) {
				TreeItem<Element> scenarItem = new TreeItem<>(new OverviewElement<>(def, Scenario.class, "Scenarios"),
						ImageHelper.getFolderIcon(Scenario.class));
				scenarioItem = getTreeItem(simulator.getScenario());
				scenarItem.getChildren().add(scenarioItem);
				scenarItem.setExpanded(true);
				rootItem.getChildren().add(scenarItem);
			}

			if (!def.getMonitors().isEmpty()) {
				TreeItem<Element> monitsItem = new TreeItem<>(new OverviewElement<>(def, Monitor.class, "Monitors"),
						ImageHelper.getFolderIcon(Monitor.class));
				for (Monitor monitor : def.getMonitors()) {
					monitsItem.getChildren().add(getTreeItem(monitor));
				}
				rootItem.getChildren().add(monitsItem);
			}

			if (!def.getComponents().isEmpty()) {
				TreeItem<Element> compsItem = new TreeItem<>(new OverviewElement<>(def, Component.class, "Components"),
						ImageHelper.getFolderIcon(Component.class));
				for (Component<?> comp : def.getComponents()) {
					compsItem.getChildren().add(getTreeItem(comp));
				}
				rootItem.getChildren().add(compsItem);
			}

			if (!def.getBehaviors().isEmpty()) {
				TreeItem<Element> behavItem = new TreeItem<>(new OverviewElement<>(def, Behavior.class, "Behaviors"),
						ImageHelper.getFolderIcon(Behavior.class));
				for (Behavior behavior : def.getBehaviors()) {
					behavItem.getChildren().add(getTreeItem(behavior));
				}
				rootItem.getChildren().add(behavItem);
			}
		} else if (component instanceof ReferenceComponent) {
			ReferenceComponent ref = (ReferenceComponent) component;

			rootItem.getChildren().add(getTreeItem(ref.getTemplate()));
		} else {
			throw new IllegalStateException("Component type not supported: " + component.getClass().getName());
		}

		return rootItem;
	}

	private TreeItem<Element> getTreeItem(Port port) {
		TreeItem<Element> result = new TreeItem<>(port, ImageHelper.getIcon(port.getClass()));

		if (port instanceof InteractionMaterialPort) {
			InteractionMaterialPort inter = (InteractionMaterialPort) port;

			if (!inter.getPorts().isEmpty()) {
				TreeItem<Element> portsItem = new TreeItem<>(
						new OverviewElement<>(inter, DefinitionPort.class, "Ports"),
						ImageHelper.getFolderIcon(Port.class));
				for (DefinitionPort def : inter.getPorts()) {
					portsItem.getChildren().add(getTreeItem(def));
				}
				result.getChildren().add(portsItem);
			}
		}

		return result;
	}

	private TreeItem<Element> getTreeItem(Scenario scenario) {
		TreeItem<Element> result = new TreeItem<>(scenario, ImageHelper.getIcon(scenario.getClass()));

		if (!scenario.getPorts().isEmpty()) {
			TreeItem<Element> scensItem = new TreeItem<>(new OverviewElement<>(scenario, DefinitionPort.class, "Ports"),
					ImageHelper.getFolderIcon(DefinitionPort.class));
			for (DefinitionPort port : scenario.getPorts()) {
				scensItem.getChildren().add(getTreeItem(port));
			}
			result.getChildren().add(scensItem);
		}

		if (!scenario.getVariables().isEmpty()) {
			TreeItem<Element> varsItem = new TreeItem<>(new OverviewElement<>(scenario, Variable.class, "Variables"),
					ImageHelper.getFolderIcon(Variable.class));
			for (Variable variable : scenario.getVariables()) {
				varsItem.getChildren().add(getTreeItem(variable));
			}
			result.getChildren().add(varsItem);
		}

		return result;
	}

	private TreeItem<Element> getTreeItem(Monitor monitor) {
		TreeItem<Element> result = new TreeItem<>(monitor, ImageHelper.getIcon(monitor.getClass()));

		if (!monitor.getVariables().isEmpty()) {
			TreeItem<Element> varsItem = new TreeItem<>(new OverviewElement<>(monitor, Variable.class, "Variables"),
					ImageHelper.getFolderIcon(Variable.class));
			for (Variable variable : monitor.getVariables()) {
				varsItem.getChildren().add(getTreeItem(variable));
			}
			result.getChildren().add(varsItem);
		}

		return result;
	}

	private TreeItem<Element> getTreeItem(Behavior behavior) {
		TreeItem<Element> result = new TreeItem<>(behavior, ImageHelper.getIcon(behavior.getClass()));

		if (!behavior.getVariables().isEmpty()) {
			TreeItem<Element> varsItem = new TreeItem<>(new OverviewElement<>(behavior, Variable.class, "Variables"),
					ImageHelper.getFolderIcon(Variable.class));
			for (Variable variable : behavior.getVariables()) {
				varsItem.getChildren().add(getTreeItem(variable));
			}
			result.getChildren().add(varsItem);
		}

		return result;
	}

	private TreeItem<Element> getTreeItem(Element port) {
		return new TreeItem<>(port, ImageHelper.getIcon(port.getClass()));
	}

}