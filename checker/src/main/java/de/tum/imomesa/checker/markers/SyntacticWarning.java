package de.tum.imomesa.checker.markers;

import de.tum.imomesa.model.Element;

public abstract class SyntacticWarning extends SyntacticMarker {

	public SyntacticWarning() {

	}

	public SyntacticWarning(Element source, String message) {
		super(source, message);
	}

}
