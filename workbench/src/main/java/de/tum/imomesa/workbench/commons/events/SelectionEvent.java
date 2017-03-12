package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.model.Element;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class SelectionEvent extends Event {
	
	public SelectionEvent() {
		
	}
	
	public SelectionEvent(Element selected) {
		this.selected.set(selected);
	}
	
	private ObjectProperty<Element> selected = new SimpleObjectProperty<>();
	
	public Element getSelected() {
		return selected.get();
	}
	
	public ObjectProperty<Element> selectedProperty() {
		return selected;
	}

}
