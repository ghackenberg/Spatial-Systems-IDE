package de.tum.imomesa.checker.listeners;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.expressions.Expression;

public class ChangeListenerMarkerClass implements ChangeListener<List<Class<?>>> {

	// data
	private SyntacticMarker marker;
	private ObjectProperty<Expression> expressions;

	// constructor
	public ChangeListenerMarkerClass(ObjectProperty<Expression> expressions, SyntacticMarker marker) {
		this.marker = marker;
		this.expressions = expressions;
	}

	@Override
	public void changed(ObservableValue<? extends List<Class<?>>> arg0, List<Class<?>> oldValue, List<Class<?>> newValue) {
		if(expressions.get() == null) return;
		
		HelperEvaluateType.evaluateType(newValue, expressions.get().getType(), marker);
	}

}
