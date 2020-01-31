package de.tum.imomesa.workbench.controllers.main;

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
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
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
		solidRoot.getChildren().clear();
		alphaRoot.getChildren().clear();

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
			paintComponent(new ModelPath<>(data), new ScenePath(solidRoot, alphaRoot));
		}
	}

	private void paintComponent(ModelPath<Component<?>> modelPath, ScenePath scenePath) {
		if (modelPath.element == null) {
			return;
		} else if (modelPath.element instanceof DefinitionComponent) {
			final DefinitionComponent definition = (DefinitionComponent) modelPath.element;

			final Group solidChild = new Group();
			final Group alphaChild = new Group();

			final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);

			// get material ports from component
			for (Port port : definition.getPorts()) {
				HelperPort.processPort(new ModelPath<>(modelPath, port), config, sceneChild);
			}

			// get material ports from scenarios
			if (modelPath.element == data) {
				for (Scenario scenario : definition.getScenarios()) {
					final ModelPath<Scenario> scenarioPath = new ModelPath<>(modelPath, scenario);
					for (Port port : scenario.getPorts()) {
						HelperPort.processPort(new ModelPath<>(scenarioPath, port), config, sceneChild);
					}
				}
			}

			// get subcomponents
			for (Component<?> subcomponent : definition.getComponents()) {
				paintComponent(new ModelPath<>(modelPath, subcomponent), sceneChild);
			}

			// get behaviors
			for (Behavior behavior : definition.getBehaviors()) {
				final ModelPath<Behavior> behaviorPath = new ModelPath<>(modelPath, behavior);
				boolean found = false;
				for (State state : behavior.getLabels()) {
					if (selected.get().getPath().contains(state)) {
						final ModelPath<State> statePath = new ModelPath<>(behaviorPath, state);
						found = true;
						for (Part part : state.getParts()) {
							HelperPart.paintPart(new ModelPath<>(statePath, part), config, null, 0, sceneChild);
						}
						break;
					}
				}
				if (!found && behavior.getInitialLabel() != null) {
					final ModelPath<State> statePath = new ModelPath<>(behaviorPath, behavior.getInitialLabel());
					for (Part part : behavior.getInitialLabel().getParts()) {
						HelperPart.paintPart(new ModelPath<>(statePath, part), config, null, 0, sceneChild);
					}
				}
			}

			// get parts from component
			for (Part part : definition.getParts()) {
				HelperPart.paintPart(new ModelPath<>(modelPath, part), config, null, 0, sceneChild);
			}

			HelperTransform.addTransforms(solidChild, definition.getTransforms());
			HelperTransform.addTransforms(alphaChild, definition.getTransforms());
		} else if (modelPath.element instanceof ReferenceComponent) {
			final ReferenceComponent reference = (ReferenceComponent) modelPath.element;

			final Group solidChild = new Group();
			final Group alphaChild = new Group();

			final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);

			paintComponent(new ModelPath<>(modelPath, reference.getTemplate()), sceneChild);

			HelperTransform.addTransforms(solidChild, reference.getTransforms());
			HelperTransform.addTransforms(alphaChild, reference.getTransforms());
		} else {
			throw new IllegalStateException("Component type not supported: " + modelPath.getClass().getName());
		}
	}
}