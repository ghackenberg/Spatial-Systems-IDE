package de.tum.imomesa.simulator.events;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.LifeMaterialPort;

public class CreateComponentEvent extends ComponentEvent {

	public CreateComponentEvent() {
		
	}
	
	public CreateComponentEvent(List<Element> context, LifeMaterialPort port, int step, ReferenceComponent component) {
		super(context, port, step, component);
	}

}
