package de.tum.imomesa.checker.markers.types;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorIncompatibleType extends SyntacticError {

	public ErrorIncompatibleType() {
		
	}
	
	public ErrorIncompatibleType(Element source, String property) {
		super(source, "Type of " + property + " is incompatible.");
	}

}
