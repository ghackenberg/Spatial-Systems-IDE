package de.tum.imomesa.checker.listeners;

import de.tum.imomesa.checker.helpers.HelperBasic;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ChangeListenerMarkerExpressionDelay implements ChangeListener<Integer> {
	
	// data
	private SyntacticMarker marker;	

	// constructor
	public ChangeListenerMarkerExpressionDelay(SyntacticMarker marker) {
		this.marker = marker;
	}
	
	@Override
	public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
		if (newValue < 0) {
			HelperBasic.addMarker(marker);
		} else {
			HelperBasic.removeMarker(marker);
		}
	}
}
