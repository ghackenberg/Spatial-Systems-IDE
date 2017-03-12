package de.tum.imomesa.workbench.controllers.simulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.ObservedElement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.attributes.nodes.AttributesGrid;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.AbstractAttributeController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AttributeController extends AbstractAttributeController implements EventHandler {

	@FXML
	private ScrollPane scrollPane;

	@FXML
	public void initialize() {
		EventBus.getInstance().subscribe(this);
	}

	// data
	private Simulator simulator;
	private List<Element> context;
	private Element element;
	private int step;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public void handle(SimulationStepEvent event) {
		step = event.getStep();

		update();
	}

	public void handle(SimulationSelectionEvent event) {
		context = event.getExtendedContext();
		element = event.getElement();

		update();
	}

	protected void update() {
		AttributesGrid grid = new AttributesGrid();

		List<Class<?>> types = new ArrayList<>();

		Class<?> type = element.getClass();

		while (Element.class.isAssignableFrom(type)) {
			types.add(type);
			type = type.getSuperclass();
		}

		for (int index = types.size() - 1; index >= 0; index--) {
			try {
				Class<?> next = types.get(index);
				grid.addType(next);
				Method method = getClass().getMethod("handle", AttributesGrid.class, List.class, next, Integer.class);
				method.invoke(this, grid, context, element, step);
			} catch (NoSuchMethodException e) {
				grid.addEmpty();
			} catch (InvocationTargetException e) {
				grid.addException(e);
			} catch (IllegalAccessException e) {
				grid.addException(e);
			}
		}

		scrollPane.setContent(grid);

	}

	public void handle(AttributesGrid grid, List<Element> context, Element element, Integer step)
			throws InterruptedException {
		grid.addProperty("Key", "" + StorageManager.getInstance().getKey(element));

		String path = "";
		for (Element item : context) {
			path += "/" + item;
		}
		grid.addProperty("Path", path);
	}

	public void handle(AttributesGrid grid, List<Element> context, NamedElement element, Integer step)
			throws InterruptedException {
		grid.addProperty("Name", element.getName());

		TextArea description = new TextArea();
		description.setDisable(true);
		description.setWrapText(true);
		description.setPrefRowCount(3);
		description.setText(element.getDescription());
		grid.addProperty("Description", description);
	}

	public void handle(AttributesGrid grid, List<Element> context, DefinitionComponent component, Integer step)
			throws InterruptedException {
		Memory memory = simulator.getMemory();

		TextField bindings = new TextField();
		bindings.setDisable(true);
		if (memory.hasBinding(context, step)) {
			bindings.setText("" + memory.getBinding(context, step));
		}
		grid.addProperty("Bindings", bindings, AttributesGrid.DYNAMIC_CELL_LABEL_STYLE, AttributesGrid.DYNAMIC_CELL_VALUE_STYLE);

		TextField transform = new TextField();
		transform.setDisable(true);
		if (memory.hasTransform(context, step)) {
			transform.setText("" + memory.getTransform(context, step));
		}
		grid.addProperty("Transform", transform, AttributesGrid.DYNAMIC_CELL_LABEL_STYLE, AttributesGrid.DYNAMIC_CELL_VALUE_STYLE);
	}

	public void handle(AttributesGrid grid, List<Element> context, Executable<?, ?> executable, Integer step)
			throws InterruptedException {
		Memory memory = simulator.getMemory();

		TextField transition = new TextField();
		transition.setDisable(true);
		if (memory.hasTransition(context, step)) {
			transition.setText("" + memory.getTransition(context, step));
		}
		grid.addProperty("Transition", transition, AttributesGrid.DYNAMIC_CELL_LABEL_STYLE, AttributesGrid.DYNAMIC_CELL_VALUE_STYLE);

		TextField label = new TextField();
		label.setDisable(true);
		if (memory.hasLabel(context, step)) {
			label.setText("" + memory.getLabel(context, step));
		}
		grid.addProperty("Label", label, AttributesGrid.DYNAMIC_CELL_LABEL_STYLE, AttributesGrid.DYNAMIC_CELL_VALUE_STYLE);
	}

	public void handle(AttributesGrid grid, List<Element> context, ObservedElement observation, Integer step)
			throws InterruptedException {
		grid.addProperty("Read", "" + observation.getReadType());
		grid.addProperty("Write", "" + observation.getWriteType());
	}

	public void handle(AttributesGrid grid, List<Element> context, Observation observation, Integer step)
			throws InterruptedException {
		Memory memory = simulator.getMemory();

		TextField value = new TextField();
		value.setDisable(true);
		if (memory.hasValue(context, step)) {
			value.setText("" + memory.getValue(null, context, step));
		}
		grid.addProperty("Value", value, AttributesGrid.DYNAMIC_CELL_LABEL_STYLE, AttributesGrid.DYNAMIC_CELL_VALUE_STYLE);
	}
}
