package de.tum.imomesa.checker.markers.lists;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorUnderfulList extends SyntacticError {
	
	public ErrorUnderfulList() {
		
	}
	
	public ErrorUnderfulList(Element source, String property, int number) {
		super(source, "At least " + number  + " " + property + " expected.");
	}

}
