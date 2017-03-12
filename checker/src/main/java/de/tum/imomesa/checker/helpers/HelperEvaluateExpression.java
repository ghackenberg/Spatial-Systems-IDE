package de.tum.imomesa.checker.helpers;

import java.util.List;

import de.tum.imomesa.checker.listeners.ChangeListenerMarkerExpressionNary;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerExpressionObservation;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerExpressionObservationType;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerExpressionUnary;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;

public class HelperEvaluateExpression {

	// ***********************************************************************************
	// Evaluate Expression
	// ***********************************************************************************
	public static void evaluateUnaryExpression(UnaryExpression parent, Expression child, SyntacticMarker marker) {
		if(child == null) return;
		
		HelperEvaluateType.evaluateType(parent.getArgumentType(), child.getType(), marker);
	}
	
	public static void evaluateNaryExpression(NaryExpression parent, List<Expression> children, SyntacticMarker marker) {
		for(Expression child : children) {
			HelperEvaluateType.evaluateType(parent.getArgumentType(), child.getType(), marker);
		}
	}
	
	// ***********************************************************************************
	// Init evaluate Expression
	//     - expressions are evaluated top down
	//     - only arguments of an expression are considered
	// ***********************************************************************************
	public static void evaluateExpressionInit(UnaryExpression parent, SyntacticMarker marker) {
		evaluateUnaryExpression(parent, parent.getArgument(), marker);
		ListenerRemover.getListenerManager().addListener(parent.argumentProperty(), new ChangeListenerMarkerExpressionUnary(parent, marker));
	}
	
	public static void evaluateExpressionInit(NaryExpression parent, SyntacticMarker marker) {
		evaluateNaryExpression(parent, parent.getArguments(), marker);
		ListenerRemover.getListenerManager().addListener(parent.argumentsProperty(), new ChangeListenerMarkerExpressionNary(parent, marker));
	}

	public static void evaluateExpressionInit(ObservationExpression parent, SyntacticMarker marker) {
		if(parent.getObservation() != null) {
			HelperEvaluateType.evaluateType(parent.getType(), parent.getObservation().getReadType(), marker);
			ListenerRemover.getListenerManager().addListener(parent.getObservation().readTypeProperty(), new ChangeListenerMarkerExpressionObservationType(parent.getType(), marker));
		}
		ListenerRemover.getListenerManager().addListener(parent.observationProperty(), new ChangeListenerMarkerExpressionObservation(parent, marker));
	}
}
