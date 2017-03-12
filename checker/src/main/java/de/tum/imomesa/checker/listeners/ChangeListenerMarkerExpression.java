package de.tum.imomesa.checker.listeners;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.expressions.Expression;

public class ChangeListenerMarkerExpression implements ChangeListener<Expression> {
	
	// data
	private SyntacticMarker marker;
	private ListProperty<Class<?>> parentType;

	// constructor
	public ChangeListenerMarkerExpression(ListProperty<Class<?>> setProperty, SyntacticMarker marker) {
		this.marker = marker;
		this.parentType = setProperty;
	}
	
	@Override
	public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
		if(newValue != null) {
			HelperEvaluateType.evaluateType(parentType.get(), newValue.getType(), marker);
		}
	}

}
