package de.tum.imomesa.checker.events;

import de.tum.imomesa.checker.markers.SyntacticMarker;


public class MarkerAddEvent extends MarkerEvent {

	public MarkerAddEvent() {
		
	}
	
	public MarkerAddEvent(SyntacticMarker marker) {
		super(marker);
	}
	
}
