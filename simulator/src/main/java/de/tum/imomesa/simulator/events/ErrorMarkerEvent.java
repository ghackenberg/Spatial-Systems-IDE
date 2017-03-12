package de.tum.imomesa.simulator.events;

import de.tum.imomesa.simulator.markers.SimulationMarker;

public class ErrorMarkerEvent extends SimulationMarkerEvent {

	public ErrorMarkerEvent() {
		
	}
	
	public ErrorMarkerEvent(SimulationMarker marker) {
		super(marker);
	}

}
