package de.tum.imomesa.checker.markers.size;

import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.model.Element;

public class ErrorNotEnoughPointsInPart extends SyntacticError {

	public ErrorNotEnoughPointsInPart() {
		
	}
	
	public ErrorNotEnoughPointsInPart(Element source) {
		super(source, "Not enough points in path (at least two points expected).");
	}

}
