package de.tum.imomesa.checker.markers;

import de.tum.imomesa.model.Element;

public abstract class SyntacticError extends SyntacticMarker {
	
	public SyntacticError() {
		
	}
	
	public SyntacticError(Element source, String message) {
		super(source, message);
	}
	
}
