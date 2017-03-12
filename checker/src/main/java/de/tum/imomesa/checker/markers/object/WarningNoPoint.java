package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningNoPoint extends SyntacticWarning {

	public WarningNoPoint() {
		
	}
	
	public WarningNoPoint(Element source) {
		super(source, "No point set.");
	}

}
