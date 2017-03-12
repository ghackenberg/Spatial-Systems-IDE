package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateExpression;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.UnaryExpression;

public class ChangeListenerMarkerExpressionUnary implements ChangeListener<Expression> {
	
	// data
	private SyntacticMarker marker;
	private UnaryExpression expression;
	
	// constructor
	public ChangeListenerMarkerExpressionUnary(UnaryExpression expression, SyntacticMarker marker) {
		this.marker = marker;
		this.expression = expression;
	}
	
	@Override
	// perform action on change
	public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
		HelperEvaluateExpression.evaluateUnaryExpression(expression, newValue, marker);
	}
}
