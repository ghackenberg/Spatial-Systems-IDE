package de.tum.imomesa.checker.markers.directions;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorInvalidDirection extends SyntacticError {

	public ErrorInvalidDirection() {
		
	}
	
	public ErrorInvalidDirection(Element source, String property) {
		super(source, "Invalid direction of " + property + ".");
	}

}
