package de.tum.imomesa.checker.markers.type;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorWrongTypeOfObservation extends SyntacticError {

	public ErrorWrongTypeOfObservation() {
		
	}
	
	public ErrorWrongTypeOfObservation(Element source) {
		super(source, "Observation has wrong type for expression.");
	}

}
