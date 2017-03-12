package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoFinalStep extends SyntacticError {

	public ErrorNoFinalStep() {
		
	}
	
	public ErrorNoFinalStep(Element source) {
		super(source, "No final step set.");
	}

}
