package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoVolume extends SyntacticError {

	public ErrorNoVolume() {
		
	}
	
	public ErrorNoVolume(Element source) {
		super(source, "No volume set.");
	}

}
