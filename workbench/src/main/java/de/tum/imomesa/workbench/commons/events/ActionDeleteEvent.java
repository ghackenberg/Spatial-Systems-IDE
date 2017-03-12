package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.Element;

public class ActionDeleteEvent extends ActionEvent {
	
	public ActionDeleteEvent() {
		
	}

	public ActionDeleteEvent(Element element) {
		super(element);
	}
	
}
