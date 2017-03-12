package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoLine extends SyntacticError {

	public ErrorNoLine() {
		
	}
	
	public ErrorNoLine(Element source) {
		super(source, "No line set.");
	}

}
