package de.tum.imomesa.workbench.explorers.handlers;

import de.tum.imomesa.model.Element;
import javafx.beans.property.ListProperty;
import javafx.scene.control.Dialog;

public class ListAddElementHandler<T extends Element> extends AbstractAddElementHandler<T, ListProperty<T>> {

	public ListAddElementHandler(Element parent, ListProperty<T> property, Class<T> type) {
		super(parent, property, type);
	}

	public ListAddElementHandler(Element parent, ListProperty<T> property, Dialog<T> dialog) {
		super(parent, property, dialog);
	}

	@Override
	protected void updateProperty(T element) {
		property.get().add(element);
	}
	
}
