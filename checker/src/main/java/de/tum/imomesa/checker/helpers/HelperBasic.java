package de.tum.imomesa.checker.helpers;

import de.tum.imomesa.checker.manager.SyntacticMarkerManager;
import de.tum.imomesa.checker.markers.SyntacticMarker;

public class HelperBasic {
	
	// ***********************************************************************************
	// Private methods to add / remove marker safely
	// ***********************************************************************************
	public static void addMarker(SyntacticMarker marker) {
		SyntacticMarkerManager.getInstance().addMarker(marker);
	}
	
	public static void removeMarker(SyntacticMarker marker) {
		SyntacticMarkerManager.getInstance().removeMarker(marker);
	}
}
