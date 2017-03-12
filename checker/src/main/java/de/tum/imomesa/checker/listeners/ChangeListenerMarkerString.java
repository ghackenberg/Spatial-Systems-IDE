package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateString;
import de.tum.imomesa.checker.markers.SyntacticMarker;

public class ChangeListenerMarkerString implements ChangeListener<String> {
	
	// data
	private SyntacticMarker marker;
	
	// constructor
	public ChangeListenerMarkerString(SyntacticMarker marker) {
		this.marker = marker;
	}
	
	@Override
	// perform action on change
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		HelperEvaluateString.evaluateString(newValue, marker);
	}
}
