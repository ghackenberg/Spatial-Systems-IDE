package de.tum.imomesa.checker.markers.strings;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningEmptyString extends SyntacticWarning {

	public WarningEmptyString() {
		
	}
	
	public WarningEmptyString(Element source, String property) {
		super(source, "No " + property + " set.");
	}

}
