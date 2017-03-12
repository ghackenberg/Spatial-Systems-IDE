package de.tum.imomesa.checker.listeners;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateObject;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;

public class ChangeListenerMarkerObject<T extends Element> implements ChangeListener<T> {
	
	// data
	private SyntacticMarker marker;
	private BooleanExpression condition;
	private T currentValue;

	// constructor
	public ChangeListenerMarkerObject(SyntacticMarker marker, T value) {
		this(marker, new SimpleBooleanProperty(true), value);
	}
	
	public ChangeListenerMarkerObject(SyntacticMarker marker, BooleanExpression condition, T value) {
		this.marker = marker;
		this.condition = condition;
		this.currentValue = value;
		
		// add listener on condition
		condition.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				HelperEvaluateObject.evaluateObject(currentValue, marker, newValue);
			}
		});
	}

	
	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		currentValue = newValue;

		HelperEvaluateObject.evaluateObject(newValue, marker, condition.get());
	}
}
