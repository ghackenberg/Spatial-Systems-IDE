package de.tum.imomesa.checker.markers.numbers;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningZeroValue extends SyntacticWarning {

	public WarningZeroValue() {
		
	}
	
	public WarningZeroValue(Element source, String property) {
		super(source, "Zero " + property + ".");
	}

}
