package de.tum.imomesa.workbench.controllers.main;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.workbench.commons.events.SelectionDiagramEvent;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.controllers.AbstractSceneController;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import de.tum.imomesa.workbench.scenes.helpers.HelperPart;
import de.tum.imomesa.workbench.scenes.helpers.HelperPort;
import de.tum.imomesa.workbench.scenes.helpers.HelperTransform;
import de.tum.imomesa.workbench.scenes.managers.ListenerManager;
import javafx.scene.Group;

public class SceneController extends AbstractSceneController {

	public void handle(SelectionExplorerEvent event) {
		if (event.getSelected() instanceof OverviewElement) {
			selected.set(event.getSelected().getParent());
		} else {
			selected.set(event.getSelected());
		}
		select(event.getSelected());
	}

	public void handle(SelectionDiagramEvent event) {
		if (event.getSelected() instanceof OverviewElement) {
			selected.set(event.getSelected().getParent());
		} else {
			selected.set(event.getSelected());
		}
		select(data);
	}

	private void select(Element element) {
		if (data != null) {
			// Unbind transform values
			rotationTransformX.angleProperty().unbindBidirectional(data.cameraAngleXProperty());
			rotationTransformY.angleProperty().unbindBidirectional(data.cameraAngleYProperty());
			translateDistance.xProperty().unbindBidirectional(data.cameraTranslateXProperty());
			translateDistance.yProperty().unbindBidirectional(data.cameraTranslateYProperty());
			translateDistance.zProperty().unbindBidirectional(data.cameraDistanceProperty());
			// Reset transform values
			rotationTransformX.setAngle(-45);
			rotationTransformY.setAngle(-45);
			translateDistance.setX(0);
			translateDistance.setY(0);
			translateDistance.setZ(-200);
		}

		// cast data
		if (element == null) {
			data = null;
		} else if (element instanceof Workspace) {
			data = null;
		} else if (element instanceof Project) {
			data = ((Project) element).getComponent();
		} else {
			data = element.getFirstAncestorByType(Component.class);
			// data = element.getLastAncestorByType(Component.class);
		}

		// Clear scene
		componentVisualization.getChildren().clear();
		// Remove listeners
		ListenerManager.getListenerRemover().removeAllListener();

		if (data != null) {
			// Initialize transforms
			rotationTransformX.setAngle(data.getCameraAngleX());
			rotationTransformY.setAngle(data.getCameraAngleY());
			translateDistance.setX(data.getCameraTranslateX());
			translateDistance.setY(data.getCameraTranslateY());
			translateDistance.setZ(data.getCameraDistance());
			// Bind transform values
			rotationTransformX.angleProperty().bindBidirectional(data.cameraAngleXProperty());
			rotationTransformY.angleProperty().bindBidirectional(data.cameraAngleYProperty());
			translateDistance.xProperty().bindBidirectional(data.cameraTranslateXProperty());
			translateDistance.yProperty().bindBidirectional(data.cameraTranslateYProperty());
			translateDistance.zProperty().bindBidirectional(data.cameraDistanceProperty());
			// Paint component
			componentVisualization.getChildren().add(paintComponent(new ArrayList<>(), data));
		}
	}

	private Group paintComponent(List<Element> context, Component<?> component) {
		if (component == null) {
			return new Group();
		} else if (component instanceof DefinitionComponent) {
			DefinitionComponent definition = (DefinitionComponent) component;

			Group group = new Group();

			// get material ports from component
			for (Port port : definition.getPorts()) {
				group.getChildren().add(HelperPort.processPort(component.append(context), port, config));
			}

			// get material ports from scenarios
			if (component == data) {
				for (Scenario scenario : definition.getScenarios()) {
					for (Port port : scenario.getPorts()) {
						group.getChildren()
								.add(HelperPort.processPort(scenario.append(component.append(context)), port, config));
					}
				}
			}

			// get subcomponents
			for (Component<?> subcomponent : definition.getComponents()) {
				group.getChildren().add(paintComponent(component.append(context), subcomponent));
			}

			// get behaviors
			for (Behavior behavior : definition.getBehaviors()) {
				boolean found = false;
				for (State state : behavior.getLabels()) {
					if (selected.get().getPath().contains(state)) {
						found = true;
						for (Part part : state.getParts()) {
							group.getChildren().add(HelperPart
									.paintPart(state.append(behavior.append(component.append(context))), part, config));
						}
						break;
					}
				}
				if (!found && behavior.getInitialLabel() != null) {
					for (Part part : behavior.getInitialLabel().getParts()) {
						group.getChildren()
								.add(HelperPart.paintPart(
										behavior.getInitialLabel().append(behavior.append(component.append(context))),
										part, config));
					}
				}
			}

			// get parts from component
			for (Part part : definition.getParts()) {
				group.getChildren().add(HelperPart.paintPart(component.append(context), part, config));
			}

			HelperTransform.addTransforms(group, definition.getTransforms());

			return group;
		} else if (component instanceof ReferenceComponent) {
			ReferenceComponent reference = (ReferenceComponent) component;

			Group group = new Group();

			group.getChildren().add(paintComponent(component.append(context), reference.getTemplate()));

			HelperTransform.addTransforms(group, component.getTransforms());

			return group;
		} else {
			throw new IllegalStateException("Component type not supported: " + component.getClass().getName());
		}
	}
}