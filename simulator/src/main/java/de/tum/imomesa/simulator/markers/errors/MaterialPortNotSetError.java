package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class MaterialPortNotSetError extends ErrorMarker {

	public MaterialPortNotSetError() {
		
	}
	
	public MaterialPortNotSetError(List<Element> context, int step) {
		super(context, "Material entry port requires set value!", step);
	}

}
