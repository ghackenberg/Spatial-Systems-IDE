package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.model.Element;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class ActionEvent extends Event {

	public ActionEvent() {
		
	}
	
	public ActionEvent(Element element) {
		this.element.set(element);
	}

	private ObjectProperty<Element> element = new SimpleObjectProperty<>();

	public Element getElement() {
		return element.get();
	}
	
	public ObjectProperty<Element> elementProperty() {
		return element;
	}
	
}
