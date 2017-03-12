package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoDefaultExpression extends SyntacticError {

	public ErrorNoDefaultExpression() {
		
	}
	
	public ErrorNoDefaultExpression(Element source) {
		super(source, "No default expression set.");
	}

}
