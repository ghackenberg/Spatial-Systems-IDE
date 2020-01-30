package de.tum.imomesa.checker.markers.objects;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNullObject extends SyntacticError {

	public ErrorNullObject() {
		
	}
	
	public ErrorNullObject(Element source, String property) {
		super(source, "No " + property + " defined.");
	}

}
