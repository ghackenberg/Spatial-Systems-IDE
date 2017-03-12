package de.tum.imomesa.workbench.explorers.handlers;

import de.tum.imomesa.model.Element;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Dialog;

public class ObjectAddElementHandler<T extends Element> extends AbstractAddElementHandler<T, ObjectProperty<T>> {
	
	public ObjectAddElementHandler(Element parent, ObjectProperty<T> property, Class<T> type) {
		super(parent, property, type);
	}
	
	public ObjectAddElementHandler(Element parent, ObjectProperty<T> property, Dialog<T> dialog) {
		super(parent, property, dialog);
	}

	@Override
	protected void updateProperty(T element) {
		property.set(element);
	}
	
}
