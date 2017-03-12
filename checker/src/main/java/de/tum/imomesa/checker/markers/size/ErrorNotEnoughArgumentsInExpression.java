package de.tum.imomesa.checker.markers.size;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNotEnoughArgumentsInExpression extends SyntacticError {
	
	public ErrorNotEnoughArgumentsInExpression() {
		
	}
	
	public ErrorNotEnoughArgumentsInExpression(Element source, int number) {
		super(source, "Not enough arguments in expression. At least " + number  + " expected.");
	}

}
