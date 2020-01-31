package de.tum.imomesa.workbench.controllers.simulation;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.AbstractSceneController;
import de.tum.imomesa.workbench.scenes.helpers.HelperPart;
import de.tum.imomesa.workbench.scenes.helpers.HelperPort;
import de.tum.imomesa.workbench.scenes.managers.ListenerManager;
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
import eu.mihosoft.jcsg.CSG;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class SceneController extends AbstractSceneController implements EventHandler {

	private List<Element> context;
	private Simulator simulator;
	private int step;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public void handle(SimulationStepEvent event) {
		paintScene(new ModelPath<>(context, data), step = event.getStep());
	}

	public void handle(SimulationSelectionEvent event) {
		paintScene(new ModelPath<>(event.getContext(), event.getComponent()), step);
	}

	public void paintScene(ModelPath<Component<?>> modelPath, int step) {
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

		// get memory and step
		Memory memory = simulator.getMemory();

		// remember component
		data = modelPath.element;
		// remeber selected
		selected.set(modelPath.element);
		// remember context
		this.context = modelPath.path;

		// clear scene and remove all listener
		solidRoot.getChildren().clear();
		alphaRoot.getChildren().clear();

		ListenerManager.getListenerRemover().removeAllListener();

		// paint new data
		if (data != null) {
			// Initialize transforms
			rotationTransformX.setAngle(data.getCameraAngleX());
			rotationTransformY.setAngle(data.getCameraAngleY());

			translateDistance.setX(data.getCameraTranslateX());
			translateDistance.setY(data.getCameraTranslateY());
			translateDistance.setZ(data.getCameraDistance());
			// Bind transforms
			rotationTransformX.angleProperty().bindBidirectional(data.cameraAngleXProperty());
			rotationTransformY.angleProperty().bindBidirectional(data.cameraAngleYProperty());

			translateDistance.xProperty().bindBidirectional(data.cameraTranslateXProperty());
			translateDistance.yProperty().bindBidirectional(data.cameraTranslateYProperty());
			translateDistance.zProperty().bindBidirectional(data.cameraDistanceProperty());

			// Paint components
			try {
				if (step >= 0) {
					// Intersections
					for (CSG csg : memory.getIntersections(step)) {
						final Group alphaChild = new Group();

						csg.toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
							meshView.setMaterial(new PhongMaterial(Color.RED));
							meshView.setDrawMode(DrawMode.FILL);
							meshView.setCullFace(CullFace.BACK);

							alphaChild.getChildren().add(meshView);
						});

						csg.toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
							meshView.setMaterial(new PhongMaterial(Color.BLACK));
							meshView.setDrawMode(DrawMode.LINE);
							meshView.setCullFace(CullFace.FRONT);

							alphaChild.getChildren().add(meshView);
						});

						alphaRoot.getChildren().add(alphaChild);
					}
					// Selected component
					paintComponent(modelPath, new ScenePath(solidRoot, alphaRoot), memory, step);
					// Reference components
					for (ReferenceComponent proxy : memory.getProxy(step)) {
						paintComponent(new ModelPath<>(proxy), new ScenePath(solidRoot, alphaRoot), memory, step);
					}
				}
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private void paintComponent(ModelPath<Component<?>> modelPath, ScenePath scenePath, Memory memory, int step) throws InterruptedException {
		if (modelPath.element == null) {
			return;
		} else if (modelPath.element instanceof DefinitionComponent) {
			final DefinitionComponent definition = (DefinitionComponent) modelPath.element;

			final Group solidChild = new Group();
			final Group alphaChild = new Group();

			final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);

			// get material ports from component
			for (DefinitionPort port : definition.getPorts()) {
				HelperPort.processPort(new ModelPath<>(modelPath, port), config, sceneChild);
			}

			// get material ports from scenario
			for (Scenario scenario : definition.getScenarios()) {
				// only display material ports of current scenario
				if (scenario.equals(simulator.getScenario())) {
					final ModelPath<Scenario> scenarioPath = new ModelPath<>(modelPath, scenario);
					for (Port port : scenario.getPorts()) {
						HelperPort.processPort(new ModelPath<>(scenarioPath, port), config, sceneChild);
					}
				}
			}

			// paint part of current state in behavior
			for (Behavior behavior : definition.getBehaviors()) {
				final ModelPath<Behavior> behaviorPath = new ModelPath<>(modelPath, behavior);
				final State state = (State) memory.getLabel(behaviorPath.path, step);
				final ModelPath<State> statePath = new ModelPath<>(behaviorPath, state);
				for (Part part : state.getParts()) {
					HelperPart.paintPart(new ModelPath<>(statePath, part), config, memory, step, sceneChild);
				}
			}

			// get subcomponents
			for (Component<?> subcomponent : definition.getComponents()) {
				paintComponent(new ModelPath<>(modelPath, subcomponent), scenePath, memory, step);
			}

			// get parts from component
			// add transform on those parts as transforms only affect the own
			// parts
			for (Part part : definition.getParts()) {
				HelperPart.paintPart(new ModelPath<>(modelPath, part), config, memory, step, sceneChild);
			}

			// transform component
			final RealMatrix matrix = memory.getTransform(modelPath.path, step); // Utils.calcTransforms(component, context, memory, step);

			final Transform transform = new Affine(matrix.getEntry(0, 0), matrix.getEntry(0, 1), matrix.getEntry(0, 2), matrix.getEntry(0, 3), matrix.getEntry(1, 0), matrix.getEntry(1, 1), matrix.getEntry(1, 2), matrix.getEntry(1, 3), matrix.getEntry(2, 0), matrix.getEntry(2, 1), matrix.getEntry(2, 2), matrix.getEntry(2, 3));

			solidChild.getTransforms().add(transform);
			alphaChild.getTransforms().add(transform);
		} else if (modelPath.element instanceof ReferenceComponent) {
			// cast component
			ReferenceComponent reference = (ReferenceComponent) modelPath.element;
			// paint component
			paintComponent(new ModelPath<>(modelPath, reference.getTemplate()), scenePath, memory, step);
		} else {
			throw new IllegalStateException("Component type not supported: " + modelPath.getClass().getName());
		}
	}

}