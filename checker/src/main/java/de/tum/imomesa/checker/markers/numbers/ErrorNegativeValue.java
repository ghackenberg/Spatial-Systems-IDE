package de.tum.imomesa.checker.markers.numbers;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNegativeValue extends SyntacticError {

	public ErrorNegativeValue() {
		
	}
	
	public ErrorNegativeValue(Element expression, String property) {
		super(expression, "Negative " + property + " selected.");
	}
	
}
