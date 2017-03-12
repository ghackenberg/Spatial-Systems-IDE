package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.Element;

public class ActionModifyEvent extends ActionEvent {

	public ActionModifyEvent() {
		
	}
	
	public ActionModifyEvent(Element element) {
		super(element);
	}

}
