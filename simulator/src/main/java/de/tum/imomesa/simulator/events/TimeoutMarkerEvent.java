package de.tum.imomesa.simulator.events;

import de.tum.imomesa.simulator.markers.SimulationMarker;

public class TimeoutMarkerEvent extends SimulationMarkerEvent {

	public TimeoutMarkerEvent() {
		
	}
	
	public TimeoutMarkerEvent(SimulationMarker marker) {
		super(marker);
	}

}
