package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.Element;

public class ActionCreateEvent extends ActionEvent {
	
	public ActionCreateEvent() {
		
	}
	
	public ActionCreateEvent(Element element) {
		super(element);
	}

}
