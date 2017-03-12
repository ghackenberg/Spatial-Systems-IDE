package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ItemNoElementError extends ErrorMarker {

	public ItemNoElementError() {
		
	}
	
	public ItemNoElementError(List<Element> context, Object item, int step) {
		super(context, "List item is not an element: " + item, step);
		
		this.item.set(item);
	}
	
	private ObjectProperty<Object> item = new SimpleObjectProperty<>();
	
	public ObjectProperty<Object> itemProperty() {
		return item;
	}

}
