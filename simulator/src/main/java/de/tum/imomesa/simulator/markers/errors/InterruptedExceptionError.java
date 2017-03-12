package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class InterruptedExceptionError extends ErrorMarker {

	public InterruptedExceptionError() {
		
	}
	
	public InterruptedExceptionError(List<Element> context, InterruptedException e, int step) {
		super(context, e.getMessage(), step);
	}

}
