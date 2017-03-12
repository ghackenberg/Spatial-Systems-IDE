package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoGuard extends SyntacticError {

	public ErrorNoGuard() {
		
	}
	
	public ErrorNoGuard(Element source) {
		super(source, "No guard set.");
	}

}
