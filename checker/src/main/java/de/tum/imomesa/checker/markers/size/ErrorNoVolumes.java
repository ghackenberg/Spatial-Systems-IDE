package de.tum.imomesa.checker.markers.size;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoVolumes extends SyntacticError {

	public ErrorNoVolumes() {
		
	}
	
	public ErrorNoVolumes(Element source) {
		super(source, "No volumes beneath Composite Volume.");
	}

}
