package de.tum.imomesa.checker.markers.contexts;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorInvalidContext extends SyntacticError {

	public ErrorInvalidContext() {
		
	}
	
	public ErrorInvalidContext(Element source) {
		super(source, "Invalid context.");
	}

}
