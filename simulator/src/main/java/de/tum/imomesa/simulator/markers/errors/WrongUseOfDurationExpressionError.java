package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class WrongUseOfDurationExpressionError extends ErrorMarker {

	public WrongUseOfDurationExpressionError() {
		
	}
	
	public WrongUseOfDurationExpressionError(List<Element> context, int step) {
		super(context, "Duration Expression has no executable as parent!", step);
	}

}
