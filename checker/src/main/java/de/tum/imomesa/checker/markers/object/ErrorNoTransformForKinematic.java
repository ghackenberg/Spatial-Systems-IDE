package de.tum.imomesa.checker.markers.object;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNoTransformForKinematic extends SyntacticError {

	public ErrorNoTransformForKinematic() {
		
	}
	
	public ErrorNoTransformForKinematic(Element source) {
		super(source, "No transform set for outgoing Kinematic Port.");
	}

}
