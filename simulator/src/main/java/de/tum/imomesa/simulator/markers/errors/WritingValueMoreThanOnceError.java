package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class WritingValueMoreThanOnceError extends ErrorMarker {

	public WritingValueMoreThanOnceError() {
		
	}
	
	public WritingValueMoreThanOnceError(List<Element> context, List<Element> contextValue, int step) {
		super(context, "Writing value more than once: " + contextValue, step);
		
		this.value.set(FXCollections.observableList(contextValue));
	}
	
	private ListProperty<Element> value = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public ListProperty<Element> valueProperty() {
		return value;
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		
		WritingValueMoreThanOnceError marker = (WritingValueMoreThanOnceError) other;
		
		if (!value.equals(marker.value)) {
			return false;
		}
		
		return true;
	}
	*/

}
