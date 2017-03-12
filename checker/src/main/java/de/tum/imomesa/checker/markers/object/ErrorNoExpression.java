package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoExpression extends SyntacticError {

	public ErrorNoExpression() {
		
	}
	
	public ErrorNoExpression(Element source) {
		super(source, "No expression set.");
	}

}
