package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class BlockedObservationError extends BlockedError {

	public BlockedObservationError() {
		
	}
	
	public BlockedObservationError(List<Element> context, List<Element> observation, int step) {
		super(context, "Waiting for observation: " + observation, step);
		
		this.observation.set(FXCollections.observableList(observation));
	}
	
	private ListProperty<Element> observation = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public ListProperty<Element> observationProperty() {
		return observation;
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		
		BlockedObservationError marker = (BlockedObservationError) other;
		
		if (!observation.equals(marker.observation)) {
			return false;
		}
		
		return true;
	}
	*/

}
