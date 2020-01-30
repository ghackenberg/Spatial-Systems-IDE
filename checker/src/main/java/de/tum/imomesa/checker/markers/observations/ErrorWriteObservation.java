package de.tum.imomesa.checker.markers.observations;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorWriteObservation extends SyntacticError {

	public ErrorWriteObservation() {
		
	}
	
	public ErrorWriteObservation(Element source, String property) {
		super(source, "Observation " + property + " cannot be read.");
	}

}
