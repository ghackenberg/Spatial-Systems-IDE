package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class WrongInputKinematicPortError extends ErrorMarker {

	public WrongInputKinematicPortError() {
		
	}
	
	public WrongInputKinematicPortError(List<Element> context, int step) {
		super(context, "Input of Input-Kinematic Ports has to be a matrix.", step);
	}

}
