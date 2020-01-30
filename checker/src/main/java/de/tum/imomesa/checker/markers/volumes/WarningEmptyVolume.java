package de.tum.imomesa.checker.markers.volumes;

import de.tum.imomesa.checker.markers.SyntacticWarning;
import de.tum.imomesa.model.Element;

public class WarningEmptyVolume extends SyntacticWarning {

	public WarningEmptyVolume() {
		
	}
	
	public WarningEmptyVolume(Element source) {
		super(source, "Empty volume.");
	}

}
