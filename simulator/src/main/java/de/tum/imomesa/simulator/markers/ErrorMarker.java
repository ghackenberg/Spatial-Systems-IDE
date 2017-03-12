package de.tum.imomesa.simulator.markers;

import java.util.List;

import de.tum.imomesa.model.Element;

public abstract class ErrorMarker extends SimulationMarker {

	public ErrorMarker() {
		
	}
	
	public ErrorMarker(List<Element> context, String message, int step) {
		super(context, message, step);
	}

}
