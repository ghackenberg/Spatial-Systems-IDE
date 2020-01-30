package de.tum.imomesa.checker.visitors;

import static de.tum.imomesa.checker.managers.ListenerRemover.addListListener;
import static de.tum.imomesa.checker.managers.ListenerRemover.addListener;

import java.util.Set;

import de.tum.imomesa.checker.markers.channels.ErrorWrongChannel;
import de.tum.imomesa.checker.markers.contexts.ErrorInvalidContext;
import de.tum.imomesa.checker.markers.directions.ErrorInvalidDirection;
import de.tum.imomesa.checker.markers.lists.ErrorOverfulList;
import de.tum.imomesa.checker.markers.lists.ErrorUnderfulList;
import de.tum.imomesa.checker.markers.numbers.ErrorNegativeValue;
import de.tum.imomesa.checker.markers.numbers.WarningZeroValue;
import de.tum.imomesa.checker.markers.objects.ErrorNullObject;
import de.tum.imomesa.checker.markers.observations.ErrorReadObservation;
import de.tum.imomesa.checker.markers.observations.ErrorWriteObservation;
import de.tum.imomesa.checker.markers.strings.ErrorEmptyString;
import de.tum.imomesa.checker.markers.types.ErrorIncompatibleType;
import de.tum.imomesa.checker.markers.volumes.WarningEmptyVolume;
import de.tum.imomesa.checker.rules.SyntacticRules;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.commons.Point;
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
import de.tum.imomesa.model.expressions.booleans.MaximumDurationExpression;
import de.tum.imomesa.model.expressions.booleans.compares.ObservationCardinalityCompareExpression;
import de.tum.imomesa.model.expressions.numbers.DurationExpression;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.model.properties.ConstraintProperty;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.properties.QualityProperty;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.TranslationTransform;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.utilities.visitors.MultipleMethodVisitor;

public class SyntacticCheckAddVisitor extends MultipleMethodVisitor {

	public void visit(Element element) {
		if (!(element instanceof Workspace)) {
			addListener(element.parentProperty(), SyntacticRules::notNull, new ErrorNullObject(element, "parent"));
		}
	}

	public void visit(NamedElement element) {
		addListener(element.nameProperty(), SyntacticRules::notEmpty, new ErrorEmptyString(element, "name"));
	}	

	public void visit(Observation observation) {
		addListener(observation.defaultProperty(), SyntacticRules::notNull, new ErrorNullObject(observation, "default expression"));
		
		ErrorIncompatibleType errorType = new ErrorIncompatibleType(observation, "default expression");
		
		addListener(observation.defaultProperty(), value -> SyntacticRules.compatible(observation, value), errorType);
		addListListener(observation.writeTypeProperty(), value -> SyntacticRules.compatible(observation, observation.getDefault()), errorType);
	}

	public void visit(Part part) {
		addListener(part.volumeProperty(), SyntacticRules::notNull, new ErrorNullObject(part, "volume"));
	}

	public void visit(Project project) {
		addListener(project.componentProperty(), SyntacticRules::notNull, new ErrorNullObject(project, "component"));
	}

	public void visit(StaticChannel channel) {
		addListener(channel.sourceProperty(), SyntacticRules::notNull, new ErrorNullObject(channel, "source"));
		addListener(channel.targetProperty(), SyntacticRules::notNull, new ErrorNullObject(channel, "target"));
		
		ErrorWrongChannel wrongChannel = new ErrorWrongChannel(channel);
		
		addListener(channel.sourceProperty(), value -> value == null || channel.getTarget() == null || SyntacticRules.valid(channel), wrongChannel);
		addListener(channel.targetProperty(), value -> value == null || channel.getSource() == null || SyntacticRules.valid(channel), wrongChannel);
		
		addListener(channel.sourceProperty(), value -> !SyntacticRules.inToIn(channel) || SyntacticRules.input(channel.getSource()), new ErrorInvalidDirection(channel, "source"));
		addListener(channel.sourceProperty(), value -> !SyntacticRules.inToIn(channel) || SyntacticRules.input(channel.getTarget()), new ErrorInvalidDirection(channel, "target"));
		
		addListener(channel.sourceProperty(), value -> !SyntacticRules.outToOut(channel) || SyntacticRules.output(channel.getSource()), new ErrorInvalidDirection(channel, "source"));
		addListener(channel.sourceProperty(), value -> !SyntacticRules.outToOut(channel) || SyntacticRules.output(channel.getTarget()), new ErrorInvalidDirection(channel, "target"));
		
		addListener(channel.sourceProperty(), value -> !SyntacticRules.inToOut(channel) || SyntacticRules.input(channel.getSource()), new ErrorInvalidDirection(channel, "source"));
		addListener(channel.sourceProperty(), value -> !SyntacticRules.inToOut(channel) || SyntacticRules.output(channel.getTarget()), new ErrorInvalidDirection(channel, "target"));
		
		addListener(channel.sourceProperty(), value -> !SyntacticRules.outToIn(channel) || SyntacticRules.output(channel.getSource()), new ErrorInvalidDirection(channel, "source"));
		addListener(channel.sourceProperty(), value -> !SyntacticRules.outToIn(channel) || SyntacticRules.input(channel.getTarget()), new ErrorInvalidDirection(channel, "target"));
	}

	public void visit(ReferenceComponent reference) {
		addListener(reference.templateProperty(), SyntacticRules::notNull, new ErrorNullObject(reference, "template")); 
	}

	public void visit(Action action) {
		addListener(action.observationProperty(), SyntacticRules::notNull, new ErrorNullObject(action, "observation"));
		addListener(action.expressionProperty(), SyntacticRules::notNull, new ErrorNullObject(action, "expression"));
		
		ErrorIncompatibleType errorType = new ErrorIncompatibleType(action, "expression");
		
		addListener(action.observationProperty(), value -> SyntacticRules.compatible(value, action.getExpression()), errorType);
		addListener(action.expressionProperty(), value -> SyntacticRules.compatible(action.getObservation(), value), errorType);
		
		addListener(action.observationProperty(), value -> SyntacticRules.writable(action, value), new ErrorWriteObservation(action, "observation"));
	}

	public void visit(Guard guard) {
		addListener(guard.expressionProperty(), SyntacticRules::notNull, new ErrorNullObject(guard, "expression"));
		addListener(guard.expressionProperty(), value -> SyntacticRules.compatible(Boolean.class, value), new ErrorIncompatibleType(guard, "expression"));
	}

	public void visit(Transition<?> transition) {
		addListener(transition.guardProperty(), SyntacticRules::notNull, new ErrorNullObject(transition, "guard"));
	}

	public void visit(Executable<?, ?> executable) {
		addListener(executable.initialLabelProperty(), SyntacticRules::notNull, new ErrorNullObject(executable, "initial label"));
	}

	public void visit(Scenario scenario) {
		addListener(scenario.finalLabelProperty(), SyntacticRules::notNull, new ErrorNullObject(scenario, "final step"));
	}

	public void visit(UnaryExpression exp) {
		addListener(exp.argumentProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "argument"));
		
		ErrorIncompatibleType errorType = new ErrorIncompatibleType(exp, "argument");		
		
		addListener(exp.argumentProperty(), value -> SyntacticRules.compatible(exp.getArgumentType(), value), errorType);
		addListener(exp.typeProperty(), value -> SyntacticRules.compatible(value, exp.getArgument()), errorType);
	}

	public void visit(NaryExpression exp) {
		addListListener(exp.argumentsProperty(), value -> value.size() >= 2, new ErrorUnderfulList(exp, "arguments", 2));
		
		ErrorIncompatibleType errorType = new ErrorIncompatibleType(exp, "arguments");
		
		addListListener(exp.argumentsProperty(), value -> SyntacticRules.compatible(exp.getArgumentType(), value), errorType);
		addListener(exp.argumentTypeProperty(), value -> SyntacticRules.compatible(value, exp.argumentsProperty()), errorType);
	}

	public void visit(TerniaryExpression exp) {
		addListener(exp.argumentOneProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "argument one"));
		addListener(exp.argumentTwoProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "argument two"));
		addListener(exp.argumentThreeProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "argument three"));
		
		ErrorIncompatibleType errorTypeOne = new ErrorIncompatibleType(exp, "argument one");
		ErrorIncompatibleType errorTypeTwo = new ErrorIncompatibleType(exp, "argument two");
		ErrorIncompatibleType errorTypeThree = new ErrorIncompatibleType(exp, "argument three");
		
		addListener(exp.argumentOneProperty(), value -> SyntacticRules.compatible(exp.getArgumentOneType(), value), errorTypeOne);
		addListener(exp.argumentTwoProperty(), value -> SyntacticRules.compatible(exp.getArgumentTwoType(), value), errorTypeTwo);
		addListener(exp.argumentThreeProperty(), value -> SyntacticRules.compatible(exp.getArgumentThreeType(), value), errorTypeThree);
		
		addListener(exp.argumentOneTypeProperty(), value -> SyntacticRules.compatible(value, exp.getArgumentOne()), errorTypeOne);
		addListener(exp.argumentTwoTypeProperty(), value -> SyntacticRules.compatible(value, exp.getArgumentTwo()), errorTypeTwo);
		addListener(exp.argumentThreeTypeProperty(), value -> SyntacticRules.compatible(value, exp.getArgumentThree()), errorTypeThree);
	}
	
	public void visit(DurationExpression exp) {
		addListener(exp.parentProperty(), value -> exp.getFirstAncestorByType(Executable.class) != null, new ErrorInvalidContext(exp));
	}
	
	public void visit(MaximumDurationExpression exp) {
		addListener(exp.parentProperty(), value -> exp.getFirstAncestorByType(Executable.class) != null, new ErrorInvalidContext(exp));
		addListener(exp.limitProperty(), value -> (int) value >= 0, new ErrorNegativeValue(exp, "limit"));
	}

	public void visit(ObservationExpression exp) {
		addListener(exp.observationProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "observation"));
		
		ErrorIncompatibleType typeError = new ErrorIncompatibleType(exp, "observation");
		
		addListener(exp.observationProperty(), value -> SyntacticRules.compatible(exp.getType(), value), typeError);
		addListener(exp.typeProperty(), value -> SyntacticRules.compatible(value, exp.getObservation()), typeError);
		
		addListener(exp.observationProperty(), value -> SyntacticRules.readable(exp, value), new ErrorReadObservation(exp, "observation"));
		addListener(exp.delayProperty(), value -> (int) value >= 0, new ErrorNegativeValue(exp, "delay"));
	}

	public void visit(ObservationCardinalityCompareExpression exp) {
		addListener(exp.observationProperty(), SyntacticRules::notNull, new ErrorNullObject(exp, "observation"));
		addListener(exp.observationProperty(), value -> SyntacticRules.compatible(Set.class, value), new ErrorIncompatibleType(exp, "observation"));
		addListener(exp.observationProperty(), value -> SyntacticRules.readable(exp, value), new ErrorReadObservation(exp, "observation"));
		addListener(exp.delayProperty(), value -> (int) value >= 0, new ErrorNegativeValue(exp, "delay"));
	}

	public void visit(Port port) {
		addListener(port.incomingStaticChannelsProperty(), value -> value.size() <= 1, new ErrorOverfulList(port, "incoming static channels", 1));
	}

	public void visit(ReferencePort port) {
		addListener(port.portImplementationProperty(), SyntacticRules::notNull, new ErrorNullObject(port, "port implementation"));
	}

	public void visit(KinematicEnergyPort port) {
		addListener(port.directionProperty(), value -> (int) value == Direction.INPUT.ordinal() || SyntacticRules.notNull(port.getTransform()), new ErrorNullObject(port, "transform"));
		addListener(port.transformProperty(), value -> (int) port.getDirection() == Direction.INPUT.ordinal() || SyntacticRules.notNull(value), new ErrorNullObject(port, "transform"));
	}

	public void visit(MaterialPort port) {
		addListener(port.volumeProperty(), SyntacticRules::notNull, new ErrorNullObject(port, "volume"));
	}

	public void visit(LifeMaterialPort port) {
		addListener(port.componentProperty(), SyntacticRules::notNull, new ErrorNullObject(port, "component"));
	}

	public void visit(Property property) {
		addListener(property.expressionProperty(), SyntacticRules::notNull, new ErrorNullObject(property, "expression"));
	}
	
	public void visit(ConstraintProperty constraint) {
		addListener(constraint.expressionProperty(), value -> SyntacticRules.compatible(Boolean.class, value), new ErrorIncompatibleType(constraint, "expression"));
	}
	
	public void visit(QualityProperty quality) {
		addListener(quality.expressionProperty(), value -> SyntacticRules.compatible(Double.class, value), new ErrorIncompatibleType(quality, "expression"));
	}

	public void visit(RotationTransform transform) {
		addListener(transform.rotationAxeProperty(), SyntacticRules::notNull, new ErrorNullObject(transform, "axis"));
	}

	public void visit(TranslationTransform transform) {
		addListener(transform.vectorProperty(), SyntacticRules::notNull, new ErrorNullObject(transform, "vector"));
	}
	
	public void visit(Point point) {
		addListener(point.xProperty(), value -> (double) value > 0, new WarningZeroValue(point, "z"));
		addListener(point.yProperty(), value -> (double) value > 0, new WarningZeroValue(point, "y"));
		addListener(point.zProperty(), value -> (double) value > 0, new WarningZeroValue(point, "x"));
	}
	
	public void visit(Volume volume) {
		addListener(volume.csgProperty(), value -> value.computeVolume() > 0, new WarningEmptyVolume(volume));
	}

	public void visit(CompositeVolume volume) {
		addListListener(volume.volumesProperty(), SyntacticRules::notEmpty, new ErrorUnderfulList(volume, "volumes", 1));
	}
	
	public void visit(BoxVolume box) {
		addListener(box.widthProperty(), value -> (double) value > 0, new WarningZeroValue(box, "width"));
		addListener(box.heightProperty(), value -> (double) value > 0, new WarningZeroValue(box, "height"));
		addListener(box.depthProperty(), value -> (double) value > 0, new WarningZeroValue(box, "depth"));
	}
	
	public void visit(SphereVolume sphere) {
		addListener(sphere.radiusProperty(), value -> (double) value > 0, new WarningZeroValue(sphere, "radius"));
	}
	
	public void visit(CylinderVolume cylinder) {
		addListener(cylinder.radiusProperty(), value -> (double) value > 0, new WarningZeroValue(cylinder, "radius"));
		addListener(cylinder.heightProperty(), value -> (double) value > 0, new WarningZeroValue(cylinder, "height"));
	}
	
}
