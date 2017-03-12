package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class ConstraintViolatedError extends ErrorMarker {

	public ConstraintViolatedError() {
		
	}
	
	public ConstraintViolatedError(List<Element> context, int step) {
		super(context, "Constraint is violated!", step);
	}

}
