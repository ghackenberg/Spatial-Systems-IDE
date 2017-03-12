package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningNoComponent extends SyntacticWarning {

	public WarningNoComponent() {
		
	}
	
	public WarningNoComponent(Element source) {
		super(source, "No component set.");
	}

}
