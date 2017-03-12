package de.tum.imomesa.checker.visitor;

import java.util.ArrayList;
import java.util.Arrays;

import de.tum.imomesa.checker.helpers.HelperEvaluateChannel;
import de.tum.imomesa.checker.helpers.HelperEvaluateExpression;
import de.tum.imomesa.checker.helpers.HelperEvaluateObject;
import de.tum.imomesa.checker.helpers.HelperEvaluateSize;
import de.tum.imomesa.checker.helpers.HelperEvaluateString;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.checker.markers.channel.ErrorWrongChannel;
import de.tum.imomesa.checker.markers.object.ErrorNoComponent;
import de.tum.imomesa.checker.markers.object.ErrorNoDefaultExpression;
import de.tum.imomesa.checker.markers.object.ErrorNoExpression;
import de.tum.imomesa.checker.markers.object.ErrorNoFinalStep;
import de.tum.imomesa.checker.markers.object.ErrorNoGuard;
import de.tum.imomesa.checker.markers.object.ErrorNoInitialLabel;
import de.tum.imomesa.checker.markers.object.ErrorNoLine;
import de.tum.imomesa.checker.markers.object.ErrorNoObservation;
import de.tum.imomesa.checker.markers.object.ErrorNoParent;
import de.tum.imomesa.checker.markers.object.ErrorNoPortImpl;
import de.tum.imomesa.checker.markers.object.ErrorNoTemplate;
import de.tum.imomesa.checker.markers.object.ErrorNoTransformForKinematic;
import de.tum.imomesa.checker.markers.object.ErrorNoVolume;
import de.tum.imomesa.checker.markers.object.WarningNoComponent;
import de.tum.imomesa.checker.markers.size.ErrorNoVolumes;
import de.tum.imomesa.checker.markers.size.ErrorNotEnoughArgumentsInExpression;
import de.tum.imomesa.checker.markers.size.ErrorTooManyIncomingChannels;
import de.tum.imomesa.checker.markers.string.ErrorNoName;
import de.tum.imomesa.checker.markers.type.ErrorWrongTypeOfExpression;
import de.tum.imomesa.checker.markers.type.ErrorWrongTypeOfObservation;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.ObservedElement;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
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
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.utilities.visitors.MultipleMethodVisitor;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;

public class SyntacticCheckVisitor extends MultipleMethodVisitor {

	public void visit(Element element) {
		// Step 1: return for workspace as the workspace is not considered by
		// this error
		if (element instanceof Workspace)
			return;

		// Step 2: create error and warning objects
		SyntacticError errorParentNotSet = new ErrorNoParent(element);

		// Step 3: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(element.parentProperty(), errorParentNotSet);
	}

	public void visit(NamedElement element) {
		// Step 1: create error and warning objects
		SyntacticError errorNameIsEmpty = new ErrorNoName(element);
		// SyntacticWarning warningDescriptionIsEmpty = new
		// WarningNoDescription(element);

		// Step 2: initial evaluation
		HelperEvaluateString.evaluateStringInit(element.nameProperty(), errorNameIsEmpty);
		// HelperEvaluateString.evaluateStringInit(element.descriptionProperty(),
		// warningDescriptionIsEmpty);
	}

	public void visit(Observation observation) {
		// Step 1: create error and warning objects
		SyntacticError errorDefaultExpressionNotSet = new ErrorNoDefaultExpression(observation);
		SyntacticError errorDefaultExpressionWithWrongType = new ErrorWrongTypeOfExpression(observation);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(observation.defaultProperty(), errorDefaultExpressionNotSet);
		HelperEvaluateType.evaluateTypeObservationInit(observation, observation.defaultProperty(),
				errorDefaultExpressionWithWrongType);
	}

	public void visit(ObservedElement observedElement) {
		// Step 1: create error and warning objects
		SyntacticError errorNameIsEmpty = new ErrorNoName(observedElement);
		// SyntacticWarning warningDescriptionIsEmpty = new
		// WarningNoDescription(observedElement);

		// Step 2: initial evaluation
		HelperEvaluateString.evaluateStringInit(observedElement.nameProperty(), errorNameIsEmpty);
		// HelperEvaluateString.evaluateStringInit(observedElement.descriptionProperty(),
		// warningDescriptionIsEmpty);
	}

	public void visit(Part part) {
		// Step 1: create error and warning objects
		SyntacticError errorVolumeNotSet = new ErrorNoVolume(part);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(part.volumeProperty(), errorVolumeNotSet);
	}

	public void visit(Project project) {
		// Step 1: create error and warning objects
		SyntacticWarning warningComponentEmpty = new WarningNoComponent(project);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(project.componentProperty(), warningComponentEmpty);
	}

	public void visit(StaticChannel channel) {
		// Step 1: create error and warning objects
		SyntacticError errorWrongChannel = new ErrorWrongChannel(channel);

		// Step 2: initial evaluation
		HelperEvaluateChannel.evaluateChannelInit(channel.getSource(), channel.getTarget(), errorWrongChannel);
	}

	public void visit(ReferenceComponent compProxy) {
		// Step 1: create error and warning objects
		SyntacticError errorTemplateNotSet = new ErrorNoTemplate(compProxy);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(compProxy.templateProperty(), errorTemplateNotSet);
	}

	public void visit(Action action) {
		// Step 1: create error and warning objects
		SyntacticError errorObservationEmpty = new ErrorNoObservation(action);
		SyntacticError errorExpressionEmpty = new ErrorNoExpression(action);
		SyntacticError errorExpressionType = new ErrorWrongTypeOfExpression(action);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(action.observationProperty(), errorObservationEmpty);
		HelperEvaluateObject.evaluateObjectInit(action.expressionProperty(), errorExpressionEmpty);
		HelperEvaluateType.evaluateTypeObservationInit(action.observationProperty(), action.expressionProperty(),
				errorExpressionType);
	}

	public void visit(Guard guard) {
		// Step 1: create error and warning objects
		SyntacticError errorExpressionNotSet = new ErrorNoExpression(guard);
		SyntacticError errorExpressionWithWrongType = new ErrorWrongTypeOfExpression(guard);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(guard.expressionProperty(), errorExpressionNotSet);
		HelperEvaluateType.evaluateTypeObservationInit(new ArrayList<Class<?>>(Arrays.asList(Boolean.class)),
				guard.expressionProperty(), errorExpressionWithWrongType);
	}

	public void visit(Transition<?> t) {
		// Step 1: create error and warning objects
		SyntacticError errorNoGuardSet = new ErrorNoGuard(t);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(t.guardProperty(), errorNoGuardSet);
	}

	public void visit(Executable<?, ?> behavior) {
		// Step 1: create error and warning objects
		SyntacticError errorInitialNotSet = new ErrorNoInitialLabel(behavior);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(behavior.initialLabelProperty(), errorInitialNotSet);
	}

	public void visit(Scenario scenario) {
		// Step 1: create error and warning objects
		SyntacticError errorFinalNotSet = new ErrorNoFinalStep(scenario);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(scenario.finalLabelProperty(), errorFinalNotSet);
	}

	public void visit(NaryExpression exp) {
		// Step 1: create error and warning objects
		SyntacticError errorTypeWrong = new ErrorWrongTypeOfExpression(exp);
		SyntacticError errorNotEnoughArguments = new ErrorNotEnoughArgumentsInExpression(exp, 2);

		// Step 2: initial evaluation
		HelperEvaluateExpression.evaluateExpressionInit(exp, errorTypeWrong);
		HelperEvaluateSize.evaluateMinSizeInit(exp.argumentsProperty(), 2, errorNotEnoughArguments);
	}

	public void visit(ObservationExpression exp) {
		// Step 1: create error and warning objects
		SyntacticError errorObservationIsNull = new ErrorNoObservation(exp);
		SyntacticError errorObservationHasWrongType = new ErrorWrongTypeOfObservation(exp);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(exp.observationProperty(), errorObservationIsNull);
		HelperEvaluateExpression.evaluateExpressionInit(exp, errorObservationHasWrongType);
	}

	public void visit(UnaryExpression exp) {
		// Step 1: create error and warning objects
		SyntacticError errorTypeWrong = new ErrorWrongTypeOfExpression(exp);
		SyntacticError errorNotEnoughArguments = new ErrorNotEnoughArgumentsInExpression(exp, 1);

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(exp.argumentProperty(), errorNotEnoughArguments);
		HelperEvaluateExpression.evaluateExpressionInit(exp, errorTypeWrong);
	}

	public void visit(TerniaryExpression exp) {
		// Step 1: create error and warning objects
		SyntacticError errorMissingArg1 = new ErrorNotEnoughArgumentsInExpression(exp, 3);
		SyntacticError errorMissingArg2 = new ErrorNotEnoughArgumentsInExpression(exp, 3);
		SyntacticError errorMissingArg3 = new ErrorNotEnoughArgumentsInExpression(exp, 3);
		// ExtendedError errorTypeWrong = new ExtendedError(this, "Wrong type of
		// argument.");

		// Step 2: initial evaluation
		HelperEvaluateObject.evaluateObjectInit(exp.argumentOneProperty(), errorMissingArg1);
		HelperEvaluateObject.evaluateObjectInit(exp.argumentTwoProperty(), errorMissingArg2);
		HelperEvaluateObject.evaluateObjectInit(exp.argumentThreeProperty(), errorMissingArg3);

		// TODO: add evaluation of type
		// EvaluationHelper.evaluateExpressionInit(this, markers,
		// errorTypeWrong);
		// EvaluationHelper.evaluateSizeInit(arguments, 2, markers,
		// errorNotEnoughArguments);
	}

	public void visit(Port port) {
		// Step 1: create error and warning objects
		SyntacticError errorMaxIncomingChannels = new ErrorTooManyIncomingChannels(port);

		// Step 2: initial evaluation and add listener
		HelperEvaluateSize.evaluateMaxSizeInit(port.incomingStaticChannelsProperty(), 1, errorMaxIncomingChannels);
	}

	public void visit(ReferencePort port) {
		// Step 1: create error and warning objects
		SyntacticError errorPortImplNotSet = new ErrorNoPortImpl(port);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(port.portImplementationProperty(), errorPortImplNotSet);
	}

	public void visit(KinematicEnergyPort port) {
		// Step 1: create error and warning objects
		SyntacticError errorTransformNotSet = new ErrorNoTransformForKinematic(port);

		// Step 2: initial evaluation and add listener
		BooleanBinding condition = Bindings.equal(port.directionProperty(), Direction.OUTPUT.ordinal());
		HelperEvaluateObject.evaluateObjectInit(port.transformProperty(), errorTransformNotSet, condition);
	}

	public void visit(LifeMaterialPort port) {
		// Step 1: create error and warning objects
		SyntacticError errorComponentNotSet = new ErrorNoComponent(port);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(port.componentProperty(), errorComponentNotSet);
	}

	public void visit(MaterialPort port) {
		// Step 1: create error and warning objects
		SyntacticError errorVolumeNotSet = new ErrorNoVolume(port);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(port.volumeProperty(), errorVolumeNotSet);
	}

	public void visit(Property property) {
		// Step 1: create error and warning objects
		SyntacticError errorExpressionNotSet = new ErrorNoExpression(property);
		SyntacticError errorExpressionWithWrongType = new ErrorWrongTypeOfExpression(property);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(property.expressionProperty(), errorExpressionNotSet);
		HelperEvaluateType.evaluateTypeObservationInit(property, property.expressionProperty(),
				errorExpressionWithWrongType);
	}

	public void visit(RotationTransform transform) {
		// Step 1: create error and warning objects
		SyntacticError errorLineNotSet = new ErrorNoLine(transform);

		// Step 2: initial evaluation and add listener
		HelperEvaluateObject.evaluateObjectInit(transform.rotationAxeProperty(), errorLineNotSet);
	}

	public void visit(CompositeVolume volume) {
		// Step 1: create error and warning objects
		SyntacticError errorNoVolumes = new ErrorNoVolumes(volume);

		// Step 2: initial evaluation
		HelperEvaluateSize.evaluateMinSizeInit(volume.volumesProperty(), 1, errorNoVolumes);
	}
}
