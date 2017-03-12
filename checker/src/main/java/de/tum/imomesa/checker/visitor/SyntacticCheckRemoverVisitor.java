package de.tum.imomesa.checker.visitor;

import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.manager.SyntacticMarkerManager;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.ObservedElement;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Guard;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.TerniaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.utilities.visitors.MultipleMethodVisitor;
import javafx.collections.ObservableList;

public class SyntacticCheckRemoverVisitor extends MultipleMethodVisitor {

	public void visit(Element element) {
		SyntacticMarkerManager.getInstance().removeMarkers(element);

		// remove listeners
		ListenerRemover.getListenerManager().removeAllListeners(element.parentProperty());
	}

	public void visit(NamedElement element) {
		ListenerRemover.getListenerManager().removeAllListeners(element.nameProperty());
		ListenerRemover.getListenerManager().removeAllListeners(element.descriptionProperty());
	}

	public void visit(Observation observation) {
		ListenerRemover.getListenerManager().removeAllListeners(observation.defaultProperty());
		ListenerRemover.getListenerManager().removeAllListeners((ObservableList<?>) observation.writeTypeProperty());
	}

	public void visit(ObservedElement observedElement) {
		ListenerRemover.getListenerManager().removeAllListeners(observedElement.nameProperty());
		ListenerRemover.getListenerManager().removeAllListeners(observedElement.descriptionProperty());
	}

	public void visit(Part part) {
		ListenerRemover.getListenerManager().removeAllListeners(part.volumeProperty());
	}

	public void visit(Project project) {
		ListenerRemover.getListenerManager().removeAllListeners(project.componentProperty());
	}

	public void visit(StaticChannel channel) {
		if (channel.getSource() != null) {
			ListenerRemover.getListenerManager().removeAllListeners(channel.getSource().directionProperty());
		}
		if (channel.getTarget() != null) {
			ListenerRemover.getListenerManager().removeAllListeners(channel.getTarget().directionProperty());
		}
	}

	public void visit(ReferenceComponent compProxy) {
		ListenerRemover.getListenerManager().removeAllListeners(compProxy.templateProperty());
	}

	public void visit(Action action) {
		ListenerRemover.getListenerManager().removeAllListeners(action.observationProperty());
		ListenerRemover.getListenerManager().removeAllListeners(action.expressionProperty());
		if (action.getObservation() != null) {
			ListenerRemover.getListenerManager()
					.removeAllListeners((ObservableList<?>) action.getObservation().writeTypeProperty());
		}
		ListenerRemover.getListenerManager().removeAllListeners(action.expressionProperty());
	}

	public void visit(Guard guard) {
		ListenerRemover.getListenerManager().removeAllListeners(guard.expressionProperty());
	}

	public void visit(Transition<?> t) {
		ListenerRemover.getListenerManager().removeAllListeners(t.guardProperty());
	}

	public void visit(Executable<?, ?> behavior) {
		ListenerRemover.getListenerManager().removeAllListeners(behavior.initialLabelProperty());
	}

	public void visit(Scenario scenario) {
		ListenerRemover.getListenerManager().removeAllListeners(scenario.finalLabelProperty());
	}

	public void visit(NaryExpression exp) {
		ListenerRemover.getListenerManager().removeAllListeners(exp.argumentsProperty().get());
	}

	public void visit(ObservationExpression exp) {
		ListenerRemover.getListenerManager().removeAllListeners(exp.observationProperty());
	}

	public void visit(UnaryExpression exp) {
		ListenerRemover.getListenerManager().removeAllListeners(exp.argumentProperty());
	}

	public void visit(TerniaryExpression exp) {
		ListenerRemover.getListenerManager().removeAllListeners(exp.argumentOneProperty());
		ListenerRemover.getListenerManager().removeAllListeners(exp.argumentTwoProperty());
		ListenerRemover.getListenerManager().removeAllListeners(exp.argumentThreeProperty());
	}

	public void visit(Port port) {
		ListenerRemover.getListenerManager().removeAllListeners(port.incomingStaticChannelsProperty().get());
	}

	public void visit(ReferencePort port) {
		ListenerRemover.getListenerManager().removeAllListeners(port.portImplementationProperty());
	}

	public void visit(KinematicEnergyPort port) {
		ListenerRemover.getListenerManager().removeAllListeners(port.transformProperty());
	}

	public void visit(LifeMaterialPort port) {
		ListenerRemover.getListenerManager().removeAllListeners(port.componentProperty());
	}

	public void visit(MaterialPort port) {
		ListenerRemover.getListenerManager().removeAllListeners(port.volumeProperty());
	}

	public void visit(Property property) {
		ListenerRemover.getListenerManager().removeAllListeners(property.expressionProperty());
		ListenerRemover.getListenerManager().removeAllListeners((ObservableList<?>) property.writeTypeProperty());
	}

	public void visit(RotationTransform transform) {
		ListenerRemover.getListenerManager().removeAllListeners(transform.rotationAxeProperty());
	}

	public void visit(CompositeVolume volume) {
		ListenerRemover.getListenerManager().removeAllListeners(volume.volumesProperty().get());
	}
}
