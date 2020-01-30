package de.tum.imomesa.checker.markers.strings;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorEmptyString extends SyntacticError {

	public ErrorEmptyString() {
		
	}
	
	public ErrorEmptyString(Element source, String property) {
		super(source, "No " + property + " set.");
	}

}
