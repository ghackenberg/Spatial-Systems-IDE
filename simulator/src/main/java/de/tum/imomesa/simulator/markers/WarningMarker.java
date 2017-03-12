package de.tum.imomesa.simulator.markers;

import java.util.List;

import de.tum.imomesa.model.Element;

public abstract class WarningMarker extends SimulationMarker {

	public WarningMarker() {
		
	}
	
	public WarningMarker(List<Element> context, String message, int step) {
		super(context, message, step);
	}

}
