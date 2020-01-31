package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.workbench.scenes.Configuration;
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class HelperPort {

	public static void processPort(ModelPath<Port> modelPath, Configuration config, ScenePath scenePath) {
		if (modelPath.element instanceof MaterialPort) {
			paintMaterialPort(modelPath.cast(MaterialPort.class), config, scenePath);
		} else if (modelPath.element instanceof KinematicEnergyPort) {
			paintKinematicEnergyPort(modelPath.cast(KinematicEnergyPort.class), config, scenePath);
		}
	}

	private static void paintMaterialPort(ModelPath<MaterialPort> modelPath, Configuration config, ScenePath scenePath) {
		boolean selected;

		if (config.getSelectedElement().getFirstAncestorByType(Activity.class) != null) {
			selected = containsActivity(config.getSelectedElement().getFirstAncestorByType(Activity.class), modelPath.element);
		} else if (config.getSelectedElement().getFirstAncestorByType(Transition.class) != null) {
			selected = containsTransition(config.getSelectedElement().getFirstAncestorByType(Transition.class), modelPath.element);
		} else if (config.getSelectedElement().getFirstAncestorByType(Monitor.class) != null) {
			selected = containsMonitor(config.getSelectedElement().getFirstAncestorByType(Monitor.class), modelPath.element);
		} else {
			selected = config.getSelectedElement().getPath().contains(modelPath.element);
		}

		if (modelPath.element.getFirstAncestorByType(Scenario.class) != null) {
			Scenario scenario = modelPath.element.getFirstAncestorByType(Scenario.class);
			selected = selected || (config.getSelectedElement().getPath().contains(scenario) && config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
		}

		final boolean finalSelected = selected;

		final Group solidChild = new Group();
		final Group alphaChild = new Group();

		final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);

		int r = 0, g = 0, b = 0;

		if (modelPath.element instanceof InteractionMaterialPort) {
			b = 255;
		} else if (modelPath.element instanceof EntryLifeMaterialPort) {
			g = 255;
		} else if (modelPath.element instanceof ExitLifeMaterialPort) {
			r = 255;
		} else {
			throw new IllegalStateException("Port type not supported: " + modelPath.element.getClass().getName());
		}

		final Color color = Color.rgb(r, g, b);

		PhongMaterial material = new PhongMaterial();

		material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty()).then(selected ? color : color.darker()).otherwise(color));

		modelPath.element.volumeProperty().addListener((property, oldValue, newValue) -> {
			updateVolume(modelPath, config, sceneChild, material, finalSelected);
		});

		updateVolume(modelPath, config, sceneChild, material, finalSelected);
	}

	private static void updateVolume(ModelPath<MaterialPort> modelPath, Configuration config, ScenePath scenePath, PhongMaterial material, boolean selected) {
		scenePath.solidParent.getChildren().clear();
		scenePath.alphaParent.getChildren().clear();

		if (modelPath.element.getVolume() != null) {
			HelperVolume.paintVolume(new ModelPath<>(modelPath, modelPath.element.getVolume()), material, config, selected, scenePath);
		}
	}

	private static void paintKinematicEnergyPort(ModelPath<KinematicEnergyPort> modelPath, Configuration config, ScenePath scenePath) {
		final Group solidChild = new Group();
		final Group alphaChild = new Group();

		final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);

		if (modelPath.element.getTransform() != null) {
			HelperTransform.paintTransform(new ModelPath<>(modelPath, modelPath.element.getTransform()), config.selectedElementProperty(), sceneChild);
		}
	}

	private static boolean containsExpression(Expression expression, Port port) {
		if (expression instanceof ObservationExpression) {
			Observation observation = ((ObservationExpression) expression).getObservation();

			if (observation.getParent() instanceof Observation) {
				return observation == port || observation.getParent() == port;
			} else {
				return observation == port;
			}
		} else if (expression instanceof UnaryExpression) {
			return containsExpression(((UnaryExpression) expression).getArgument(), port);
		} else if (expression instanceof NaryExpression) {
			for (Expression argument : ((NaryExpression) expression).getArguments()) {
				if (containsExpression(argument, port)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private static boolean containsTransition(Transition transition, Port port) {
		if (containsExpression(transition.getGuard().getExpression(), port)) {
			return true;
		}
		if (containsActions(transition.getActions(), port)) {
			return true;
		}
		return false;
	}

	private static boolean containsActivity(Activity activity, Port port) {
		if (containsProperties(activity.getProperties(), port)) {
			return true;
		}
		return false;
	}

	private static boolean containsActions(List<Action> actions, Port port) {
		for (Action action : actions) {
			if (action.getObservation() == port || containsExpression(action.getExpression(), port)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsProperties(List<Property> properties, Port port) {
		for (Property property : properties) {
			if (/* containsExpression(property.getDefault(), port) || */containsExpression(property.getExpression(), port)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsMonitor(Monitor monitor, Port port) {
		for (Activity activity : monitor.getLabels()) {
			if (containsActivity(activity, port)) {
				return true;
			}
		}
		for (Transition transition : monitor.getTransitions()) {
			if (containsTransition(transition, port)) {
				return true;
			}
		}
		return false;
	}

}
