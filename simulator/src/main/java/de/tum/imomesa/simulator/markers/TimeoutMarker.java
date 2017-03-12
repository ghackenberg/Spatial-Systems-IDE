package de.tum.imomesa.simulator.markers;

import java.util.ArrayList;

public class TimeoutMarker extends SimulationMarker {

	public TimeoutMarker() {
		
	}
	
	public TimeoutMarker(int step) {
		super(new ArrayList<>(), "Timeout!", step);
	}

}
