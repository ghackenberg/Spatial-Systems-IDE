package de.tum.imomesa.checker.markers.string;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningNoDescription extends SyntacticWarning {

	public WarningNoDescription() {
		
	}
	
	public WarningNoDescription(Element source) {
		super(source, "No description set.");
	}

}
