package de.tum.imomesa.simulator.events;

import de.tum.imomesa.simulator.markers.SimulationMarker;

public class WarningMarkerEvent extends SimulationMarkerEvent {

	public WarningMarkerEvent() {
		
	}
	
	public WarningMarkerEvent(SimulationMarker marker) {
		super(marker);
	}

}
