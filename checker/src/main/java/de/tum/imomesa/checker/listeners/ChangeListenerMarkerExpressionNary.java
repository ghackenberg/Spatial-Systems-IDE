package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import de.tum.imomesa.checker.helpers.HelperEvaluateExpression;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;

public class ChangeListenerMarkerExpressionNary implements ChangeListener<ObservableList<Expression>> {
	
	// data
	private SyntacticMarker marker;
	private NaryExpression expression;
	
	// constructor
	public ChangeListenerMarkerExpressionNary(NaryExpression expression, SyntacticMarker marker) {
		this.marker = marker;
		this.expression = expression;
	}
	
	@Override
	// perform action on change
	public void changed(ObservableValue<? extends ObservableList<Expression>> observable, ObservableList<Expression> oldValue, ObservableList<Expression> newValue) {
		HelperEvaluateExpression.evaluateNaryExpression(expression, newValue, marker);
	}
}
