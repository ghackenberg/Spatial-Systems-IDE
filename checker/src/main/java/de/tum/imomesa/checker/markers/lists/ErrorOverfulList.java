package de.tum.imomesa.checker.markers.lists;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorOverfulList extends SyntacticError {
	
	public ErrorOverfulList() {
		
	}
	
	public ErrorOverfulList(Element source, String property, int number) {
		super(source, "At most " + number  + " " + property + " expected.");
	}

}
