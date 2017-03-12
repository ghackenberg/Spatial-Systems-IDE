package de.tum.imomesa.checker.events;

import de.tum.imomesa.checker.markers.SyntacticMarker;

public class MarkerRemoveEvent extends MarkerEvent {

	public MarkerRemoveEvent() {
		
	}
	
	public MarkerRemoveEvent(SyntacticMarker marker) {
		super(marker);
	}

}
