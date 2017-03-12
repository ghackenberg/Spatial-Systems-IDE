package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoTemplate extends SyntacticError {

	public ErrorNoTemplate() {
		
	}
	
	public ErrorNoTemplate(Element source) {
		super(source, "No template set.");
	}

}
