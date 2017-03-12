package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoInitialLabel extends SyntacticError {

	public ErrorNoInitialLabel() {
		
	}
	
	public ErrorNoInitialLabel(Element source) {
		super(source, "No initial label set.");
	}

}
