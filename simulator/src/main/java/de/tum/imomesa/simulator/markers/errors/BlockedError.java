package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public abstract class BlockedError extends ErrorMarker {

	public BlockedError() {
		
	}
	
	public BlockedError(List<Element> context, String message, int step) {
		super(context, message, step);
	}

}
