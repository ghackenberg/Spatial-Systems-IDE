package de.tum.imomesa.checker.markers.type;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorWrongTypeOfExpression extends SyntacticError {

	public ErrorWrongTypeOfExpression() {
		
	}
	
	public ErrorWrongTypeOfExpression(Element source) {
		super(source, "Type of expression is wrong.");
	}

}
