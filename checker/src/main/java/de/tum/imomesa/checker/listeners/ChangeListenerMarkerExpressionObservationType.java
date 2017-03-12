package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.markers.SyntacticMarker;

public class ChangeListenerMarkerExpressionObservationType implements ChangeListener<Class<?>>  {
	
	private Class<?> typeParent;
	private SyntacticMarker marker;
	
	public ChangeListenerMarkerExpressionObservationType(Class<?> typeParent, SyntacticMarker marker) {
		this.typeParent = typeParent;
		this.marker = marker;
	}

	@Override
	public void changed(ObservableValue<? extends Class<?>> observable, Class<?> oldValue, Class<?> newValue) {
		HelperEvaluateType.evaluateType(typeParent, newValue, marker);
	}

}
