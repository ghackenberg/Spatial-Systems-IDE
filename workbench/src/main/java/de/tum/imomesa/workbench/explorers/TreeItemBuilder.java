package de.tum.imomesa.workbench.explorers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Guard;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.TerniaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.explorers.listeners.DefinitionComponentChangeListener;
import de.tum.imomesa.workbench.explorers.listeners.ElementChangeListener;
import de.tum.imomesa.workbench.explorers.listeners.OverviewElementChangeListener;
import de.tum.imomesa.workbench.explorers.listeners.ReferenceComponentChangeListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TreeItem;

public class TreeItemBuilder {

	private static TreeItemBuilder INSTANCE = new TreeItemBuilder();

	public static TreeItemBuilder getInstance() {
		return INSTANCE;
	}

	// Constructor

	private TreeItemBuilder() {

	}

	// Transform (element)

	public <T extends Element> TreeItem<Element> transform(T object, Configuration configuration) {
		return transform(object, configuration, false);
	}

	public <T extends Element> TreeItem<Element> transform(T object, Configuration configuration, boolean expanded) {
		if (object == null) {
			throw new IllegalStateException();
		}

		TreeItem<Element> item = new TreeItem<>(object, ImageHelper.getIcon(object.getClass()));

		dispatch(object, item, configuration);

		item.setExpanded(expanded);

		return item;
	}

	// Transform (child)

	public <T extends Element> TreeItem<Element> transform(Class<T> type, ObjectProperty<T> property,
			Configuration configuration) {
		return transform(type, property, configuration, false);
	}

	public <T extends Element> TreeItem<Element> transform(Class<T> type, ObjectProperty<T> property,
			Configuration configuration, boolean expanded) {
		T object = property.get();

		Class<?> internalType = type;

		if (object != null) {
			internalType = object.getClass();
		}

		TreeItem<Element> item = new TreeItem<>(object, ImageHelper.getIcon(internalType));

		if (object != null) {
			dispatch(object, item, configuration);
		}

		property.addListener(new ElementChangeListener<>(type, item, configuration));

		item.setExpanded(expanded);

		return item;
	}

	// Transform (overview)

	public <T extends Element, S extends T> TreeItem<Element> transform(Element parent, String name, Class<T> type,
			ListProperty<S> list, Configuration configuration) {
		return transform(parent, name, type, list, configuration, false, false);
	}

	public <T extends Element, S extends T> TreeItem<Element> transform(Element parent, String name, Class<T> type,
			ListProperty<S> list, Configuration configuration, boolean expanded, boolean childrenExpanded) {
		@SuppressWarnings("unchecked")
		OverviewElement<T> element = new OverviewElement<T>(parent, type, (ListProperty<T>) list, name);

		TreeItem<Element> item = new TreeItem<>(element, ImageHelper.getFolderIcon(type));

		for (T child : list) {
			item.getChildren().add(transform(child, configuration, childrenExpanded));
		}

		list.addListener(new OverviewElementChangeListener(item, configuration, childrenExpanded));

		item.setExpanded(expanded);

		return item;
	}

	// Dispatch

	public <T extends Element> void dispatch(T object, TreeItem<Element> item, Configuration configuration) {
		if (object != null) {
			List<Method> methods = new ArrayList<>();
			Class<?> iterator = object.getClass();
			while (iterator != null) {
				try {
					methods.add(TreeItemBuilder.class.getDeclaredMethod("process", iterator, TreeItem.class,
							Configuration.class));
				} catch (Exception e) {
					// ignore
				} finally {
					iterator = iterator.getSuperclass();
				}
			}
			for (int i = methods.size() - 1; i >= 0; i--) {
				try {
					methods.get(i).invoke(this, object, item, configuration);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	// Process

	public void process(Activity a, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(a, "Actions", Action.class, a.actionsProperty(), configuration, true, false));
		item.getChildren()
				.add(transform(a, "Properties", Property.class, a.propertiesProperty(), configuration, true, false));
	}

	public void process(Behavior b, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(b, "States", State.class, b.labelsProperty(), configuration, true, false));
		item.getChildren().add(transform(b, "Transitions", de.tum.imomesa.model.executables.behaviors.Transition.class,
				b.transitionsProperty(), configuration, true, false));
		item.getChildren()
				.add(transform(b, "Variables", Variable.class, b.variablesProperty(), configuration, true, false));
	}

	public void process(DefinitionComponent component, TreeItem<Element> item, Configuration configuration) {
		DefinitionComponentChangeListener listener = new DefinitionComponentChangeListener(component, item,
				configuration);

		configuration.requirementsProperty().addListener(listener);
		configuration.portsProperty().addListener(listener);
		configuration.propertiesProperty().addListener(listener);
		configuration.scenariosProperty().addListener(listener);
		configuration.monitorsProperty().addListener(listener);
		configuration.componentsProperty().addListener(listener);
		configuration.channelsProperty().addListener(listener);
		configuration.behaviorsProperty().addListener(listener);
		configuration.partsProperty().addListener(listener);
		configuration.transformsProperty().addListener(listener);
	}

	public void process(ReferenceComponent component, TreeItem<Element> item, Configuration configuration) {
		ReferenceComponentChangeListener listener = new ReferenceComponentChangeListener(component, item,
				configuration);

		configuration.transformsProperty().addListener(listener);
	}

	public void process(UnaryExpression e, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, e.argumentProperty(), configuration));
	}

	public void process(NaryExpression e, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(e, "Arguments", Expression.class, e.argumentsProperty(), configuration, true, false));
	}

	public void process(TerniaryExpression e, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, e.argumentOneProperty(), configuration));
		item.getChildren().add(transform(Expression.class, e.argumentTwoProperty(), configuration));
		item.getChildren().add(transform(Expression.class, e.argumentThreeProperty(), configuration));
	}

	public void process(Monitor m, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(m, "Activities", Activity.class, m.labelsProperty(), configuration, true, false));
		item.getChildren().add(transform(m, "Transitions", de.tum.imomesa.model.executables.monitors.Transition.class,
				m.transitionsProperty(), configuration, true, false));
		item.getChildren()
				.add(transform(m, "Variables", Variable.class, m.variablesProperty(), configuration, true, false));
	}

	public void process(Part p, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Volume.class, p.volumeProperty(), configuration));
		item.getChildren()
				.add(transform(p, "Transforms", Transform.class, p.transformsProperty(), configuration, true, false));
	}

	public void process(Observation observation, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, observation.defaultProperty(), configuration));
	}

	public void process(MaterialPort port, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Volume.class, port.volumeProperty(), configuration));
	}

	public void process(InteractionMaterialPort port, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(port, "Ports", DefinitionPort.class, port.portsProperty(), configuration, true, false));
	}

	public void process(KinematicEnergyPort port, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Transform.class, port.transformProperty(), configuration));
	}

	public void process(Property property, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, property.expressionProperty(), configuration));
	}

	public void process(Project project, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(project, "Templates", DefinitionComponent.class, project.templatesProperty(),
				configuration, true, false));
		item.getChildren().add(transform(DefinitionComponent.class, project.componentProperty(), configuration));
	}

	public void process(Requirement requirement, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(requirement, "Requirements", Requirement.class,
				requirement.requirementsProperty(), configuration));
	}

	public void process(Scenario scenario, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(scenario, "Ports", LifeMaterialPort.class, scenario.portsProperty(),
				configuration, true, false));
		item.getChildren()
				.add(transform(scenario, "Steps", Step.class, scenario.labelsProperty(), configuration, true, false));
		item.getChildren()
				.add(transform(scenario, "Transitions", de.tum.imomesa.model.executables.scenarios.Transition.class,
						scenario.transitionsProperty(), configuration, true, false));
		item.getChildren().add(transform(scenario, "Variables", Variable.class, scenario.variablesProperty(),
				configuration, true, false));
	}

	public void process(State state, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(state, "Actions", Action.class, state.actionsProperty(), configuration, true, false));
		item.getChildren().add(
				transform(state, "Properties", Property.class, state.propertiesProperty(), configuration, true, false));
		item.getChildren()
				.add(transform(state, "Parts", Part.class, state.partsProperty(), configuration, true, false));
	}

	public void process(Step step, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(step, "Actions", Action.class, step.actionsProperty(), configuration, true, false));
		item.getChildren().add(
				transform(step, "Properties", Property.class, step.propertiesProperty(), configuration, true, false));
	}

	public void process(Transition<?> transition, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Guard.class, transition.guardProperty(), configuration));
		item.getChildren().add(transform(transition, "Actions", Action.class, transition.actionsProperty(),
				configuration, true, false));
	}

	public void process(Guard guard, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, guard.expressionProperty(), configuration));
	}

	public void process(Action action, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(Expression.class, action.expressionProperty(), configuration));
	}

	public void process(Volume volume, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(volume, "Transforms", Transform.class, volume.transformsProperty(),
				configuration, true, false));
	}

	public void process(CompositeVolume volume, TreeItem<Element> item, Configuration configuration) {
		item.getChildren()
				.add(transform(volume, "Volumes", Volume.class, volume.volumesProperty(), configuration, true, false));
	}

	public void process(Workspace workspace, TreeItem<Element> item, Configuration configuration) {
		item.getChildren().add(transform(workspace, "Projects", Project.class, workspace.projectsProperty(),
				configuration, true, true));
	}

}
