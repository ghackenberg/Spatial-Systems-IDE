package de.tum.imomesa.checker.markers.string;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoName extends SyntacticError {

	public ErrorNoName() {
		
	}
	
	public ErrorNoName(Element source) {
		super(source, "No name set.");
	}

}
