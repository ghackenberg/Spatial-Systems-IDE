package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoComponent extends SyntacticError {

	public ErrorNoComponent() {
		
	}
	
	public ErrorNoComponent(Element source) {
		super(source, "No component set.");
	}

}
