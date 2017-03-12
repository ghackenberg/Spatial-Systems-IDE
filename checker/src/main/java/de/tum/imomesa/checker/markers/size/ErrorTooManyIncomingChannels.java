package de.tum.imomesa.checker.markers.size;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorTooManyIncomingChannels extends SyntacticError {

	public ErrorTooManyIncomingChannels() {
		
	}
	
	public ErrorTooManyIncomingChannels(Element source) {
		super(source, "Too many incoming channels on port.");
	}

}
