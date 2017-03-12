package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoObservation extends SyntacticError {

	public ErrorNoObservation() {
		
	}
	
	public ErrorNoObservation(Element source) {
		super(source, "No observation set.");
	}

}
