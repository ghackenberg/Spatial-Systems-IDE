package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
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
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class HelperPort {

	public static Node processPort(List<Element> context, Port port, Configuration config) {
		if (port instanceof MaterialPort) {
			return paintPort(context, (MaterialPort) port, config);
		} else if (port instanceof KinematicEnergyPort) {
			return paintPort(context, (KinematicEnergyPort) port, config);
		} else {
			return new Group();
		}
	}

	private static Node paintPort(List<Element> context, MaterialPort port, Configuration config) {
		boolean selected;

		if (config.getSelectedElement().getFirstAncestorByType(Activity.class) != null) {
			selected = containsActivity(config.getSelectedElement().getFirstAncestorByType(Activity.class), port);
		} else if (config.getSelectedElement().getFirstAncestorByType(Transition.class) != null) {
			selected = containsTransition(config.getSelectedElement().getFirstAncestorByType(Transition.class), port);
		} else if (config.getSelectedElement().getFirstAncestorByType(Monitor.class) != null) {
			selected = containsMonitor(config.getSelectedElement().getFirstAncestorByType(Monitor.class), port);
		} else {
			selected = config.getSelectedElement().getPath().contains(port);
		}

		if (port.getFirstAncestorByType(Scenario.class) != null) {
			Scenario scenario = port.getFirstAncestorByType(Scenario.class);
			selected = selected || (config.getSelectedElement().getPath().contains(scenario) && config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
		}
		
		final boolean finalSelected = selected;
		
		Group group = new Group();
		
		int r = 0, g = 0, b = 0;

		if (port instanceof InteractionMaterialPort) {
			b = 255;
		} else if (port instanceof EntryLifeMaterialPort) {
			g = 255;
		} else if (port instanceof ExitLifeMaterialPort) {
			r = 255;
		} else {
			throw new IllegalStateException("Port type not supported: " + port.getClass().getName());
		}
		
		final Color color = Color.rgb(r, g, b);
		
		PhongMaterial material = new PhongMaterial();
		
		material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty()).then(selected ? color : color.darker()).otherwise(color));

		port.volumeProperty().addListener((property, oldValue, newValue) -> {
			updateVolume(context, port, config, group, material, finalSelected);
		});
		
		updateVolume(context, port, config, group, material, finalSelected);

		return group;
	}
	
	private static void updateVolume(List<Element> context, MaterialPort port, Configuration config, Group group, PhongMaterial material, boolean selected) {
		group.getChildren().clear();
		
		if (port.getVolume() != null) {
			group.getChildren().add(HelperVolume.paintVolume(port.append(context), port.getVolume(), material, config, selected));
		}
	}

	private static Node paintPort(List<Element> context, KinematicEnergyPort port, Configuration config) {
		Group group = new Group();

		if (port.getTransform() != null) {
			group.getChildren().add(HelperTransform.paintTransform(port.append(context), port.getTransform(),
					config.selectedElementProperty()));
		}

		return group;
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
			if (containsExpression(property.getDefault(), port) || containsExpression(property.getExpression(), port)) {
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
