package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class PartCollisionError extends ErrorMarker {

	public PartCollisionError() {
		
	}
	
	public PartCollisionError(List<Element> context, List<Element> contextCollisionObject, int step) {
		super(context, "Part collision detected with " + contextCollisionObject, step);
		
		this.collisionObject.set(FXCollections.observableList(contextCollisionObject));
	}
	
	private ListProperty<Element> collisionObject = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public ListProperty<Element> collisionObjectProperty() {
		return collisionObject;
	}
	
	public List<Element> getCollisionObject() {
		return collisionObject.get();
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		
		PartCollisionError marker = (PartCollisionError) other;
		
		if (!collisionObject.equals(marker.collisionObject)) {
			return false;
		}
		
		return true;
	}
	*/

}
