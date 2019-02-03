package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperBox {

	private static final ReadOnlyBooleanWrapper ALWAYS = new ReadOnlyBooleanWrapper(true);
	
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
	
	public static Node addBox(List<Element> context, BoxVolume volume, Color color, Configuration config) {
		boolean selected = config.isElementSelected(context, volume);

		Box fillBox, lineBox;

		if (volume.getFirstAncestorByType(Part.class) != null) {
			fillBox = addBox(volume, DrawMode.FILL, CullFace.NONE, color, config, ALWAYS, selected);
			lineBox = addBox(volume, DrawMode.LINE, CullFace.FRONT, Color.BLACK, config, config.showOutlineProperty(),
					selected);
		} else if (volume.getFirstAncestorByType(Port.class) != null) {
			Port port = volume.getFirstAncestorByType(Port.class);

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

				selected = selected || (config.getSelectedElement().getPath().contains(scenario)
						&& config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
			}

			fillBox = addBox(volume, DrawMode.FILL, CullFace.NONE, color, config, new ReadOnlyBooleanWrapper(selected),
					selected);
			lineBox = addBox(volume, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK, Color.BLACK, config,
					ALWAYS, selected);
		} else {
			throw new IllegalStateException();
		}

		Group group = new Group();

		group.getChildren().add(fillBox);
		group.getChildren().add(lineBox);

		return group;
	}

	private static Box addBox(BoxVolume volume, DrawMode mode, CullFace cull, Color color, Configuration config,
			BooleanProperty visible, boolean selected) {
		PhongMaterial material = new PhongMaterial();

		if (color == null) {
			HelperColor.bindColor(material, volume, config, selected);
		} else {
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty())
					.then(selected ? color : color.darker()).otherwise(color));
		}

		Box box = new Box();

		box.setDrawMode(mode);
		box.setCullFace(cull);
		box.setMaterial(material);

		box.visibleProperty().bind(visible);
		box.widthProperty().bind(volume.widthProperty());
		box.heightProperty().bind(volume.heightProperty());
		box.depthProperty().bind(volume.depthProperty());

		HelperTransform.addTransforms(box, volume.getTransforms());

		return box;
	}

}
