package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.Element;

public class SelectionDiagramEvent extends SelectionEvent {
	
	public SelectionDiagramEvent() {
		
	}
	
	public SelectionDiagramEvent(Element selected) {
		super(selected);
	}

}
