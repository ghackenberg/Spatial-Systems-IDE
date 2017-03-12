package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class TooManyIncomingChannelsError extends ErrorMarker {

	public TooManyIncomingChannelsError() {
		
	}
	
	public TooManyIncomingChannelsError(List<Element> context, int step) {
		super(context, "Too many incoming channels.", step);
	}

}
