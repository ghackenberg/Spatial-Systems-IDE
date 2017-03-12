package de.tum.imomesa.checker.markers.channel;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorWrongChannel extends SyntacticError {

	public ErrorWrongChannel() {
		
	}
	
	public ErrorWrongChannel(Element source) {
		super(source, "Channel is wrong.");
	}

}
