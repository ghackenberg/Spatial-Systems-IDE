package de.tum.imomesa.checker.helpers;

import javafx.beans.property.StringProperty;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerString;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;

public class HelperEvaluateString {

	// ***********************************************************************************
	// Evaluate string
	// ***********************************************************************************
	public static void evaluateString(String s, SyntacticMarker marker) {
		if(s == null || s.isEmpty()) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	// ***********************************************************************************
	// Init evaluate string
	// ***********************************************************************************
	public static void evaluateStringInit(StringProperty s, SyntacticMarker marker) {
		evaluateString(s.get(), marker);
		ListenerRemover.getListenerManager().addListener(s, new ChangeListenerMarkerString(marker));
	}
}
