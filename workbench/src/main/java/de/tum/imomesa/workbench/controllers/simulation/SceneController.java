package de.tum.imomesa.workbench.controllers.simulation;

import java.util.ArrayList;
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
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.AbstractSceneController;
import de.tum.imomesa.workbench.scenes.helpers.HelperPart;
import de.tum.imomesa.workbench.scenes.helpers.HelperPort;
import de.tum.imomesa.workbench.scenes.managers.ListenerManager;
import javafx.scene.Group;
import javafx.scene.transform.Affine;

public class SceneController extends AbstractSceneController implements EventHandler {

	private List<Element> context;
	private Simulator simulator;
	private int step;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public void handle(SimulationStepEvent event) {
		paintScene(context, data, step = event.getStep());
	}

	public void handle(SimulationSelectionEvent event) {
		paintScene(event.getContext(), event.getComponent(), step);
	}

	public void paintScene(List<Element> context, Component<?> component, int step) {
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
		data = component;
		// remeber selected
		selected.set(component);
		// remember context
		this.context = context;

		// clear scene and remove all listener
		componentVisualization.getChildren().clear();
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
				paintComponent(data, context, memory, step);
				// if (context.size() == 1) {
				for (ReferenceComponent proxy : memory.getProxy(step)) {
					paintComponent(proxy, proxy.append(new ArrayList<>()), memory, step);
				}
				// }
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private void paintComponent(Component<?> component, List<Element> context, Memory memory, int step)
			throws InterruptedException {
		if (component == null) {
			return;
		} else if (component instanceof DefinitionComponent) {
			DefinitionComponent data = (DefinitionComponent) component;

			Group group = new Group();

			// get material ports from component
			for (DefinitionPort port : data.getPorts()) {
				group.getChildren().add(HelperPort.processPort(component.append(context), port, config));
			}

			// get material ports from scenario
			for (Scenario scenario : data.getScenarios()) {
				// only display material ports of current scenario
				if (scenario.equals(simulator.getScenario())) {
					for (Port port : scenario.getPorts()) {
						group.getChildren()
								.add(HelperPort.processPort(scenario.append(component.append(context)), port, config));
					}
				}
			}

			// paint part of current state in behavior
			for (Behavior behavior : data.getBehaviors()) {
				State state = (State) memory.getLabel(behavior.append(context), step);
				for (Part part : state.getParts()) {
					group.getChildren().add(HelperPart
							.paintPart(state.append(behavior.append(component.append(context))), part, config));
				}
			}

			// get subcomponents
			for (Component<?> subcomponent : data.getComponents()) {
				paintComponent(subcomponent, subcomponent.append(context), memory, step);
			}

			// get parts from component
			// add transform on those parts as transforms only affect the own
			// parts
			for (Part part : data.getParts()) {
				group.getChildren().add(HelperPart.paintPart(component.append(context), part, config));
			}

			// transform component
			RealMatrix matrix = Utils.calcTransforms(component, context, memory, step);

			group.getTransforms()
					.add(new Affine(matrix.getEntry(0, 0), matrix.getEntry(0, 1), matrix.getEntry(0, 2),
							matrix.getEntry(0, 3), matrix.getEntry(1, 0), matrix.getEntry(1, 1), matrix.getEntry(1, 2),
							matrix.getEntry(1, 3), matrix.getEntry(2, 0), matrix.getEntry(2, 1), matrix.getEntry(2, 2),
							matrix.getEntry(2, 3)));

			componentVisualization.getChildren().add(group);
		} else if (component instanceof ReferenceComponent) {
			// cast component
			ReferenceComponent proxy = (ReferenceComponent) component;
			// paint component
			paintComponent(proxy.getTemplate(), proxy.getTemplate().append(context), memory, step);
		} else {
			throw new IllegalStateException("Component type not supported: " + component.getClass().getName());
		}
	}

}