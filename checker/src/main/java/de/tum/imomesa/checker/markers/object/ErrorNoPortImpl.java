package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoPortImpl extends SyntacticError {

	public ErrorNoPortImpl() {
		
	}
	
	public ErrorNoPortImpl(Element source) {
		super(source, "No port implementation set.");
	}

}
