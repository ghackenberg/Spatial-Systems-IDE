package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.Element;

public class SelectionExplorerEvent extends SelectionEvent {
	
	public SelectionExplorerEvent() {
		
	}
	
	public SelectionExplorerEvent(Element selected) {
		super(selected);
	}

}
