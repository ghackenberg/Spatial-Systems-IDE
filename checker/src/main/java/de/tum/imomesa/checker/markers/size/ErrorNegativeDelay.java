package de.tum.imomesa.checker.markers.size;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.expressions.ObservationExpression;

public class ErrorNegativeDelay extends SyntacticError {

	public ErrorNegativeDelay() {
		
	}
	
	public ErrorNegativeDelay(ObservationExpression expression) {
		super(expression, "Delay must be zero or positive integer.");
	}
	
}
