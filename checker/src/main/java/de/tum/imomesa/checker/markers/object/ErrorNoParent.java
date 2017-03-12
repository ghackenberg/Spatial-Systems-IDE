package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoParent extends SyntacticError {

	public ErrorNoParent() {
		
	}
	
	public ErrorNoParent(Element source) {
		super(source, "No parent set.");
	}

}
