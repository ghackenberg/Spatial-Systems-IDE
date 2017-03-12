package de.tum.imomesa.simulator.events;

import de.tum.imomesa.simulator.markers.SimulationMarker;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class SimulationMarkerEvent extends SimulationEvent {
	
	public SimulationMarkerEvent() {
		
	}
	
	public SimulationMarkerEvent(SimulationMarker marker) {
		this.marker.set(marker);
	}
	
	private ObjectProperty<SimulationMarker> marker = new SimpleObjectProperty<>();
	
	public SimulationMarker getMarker() {
		return marker.get();
	}
	
	public ObjectProperty<SimulationMarker> markerProperty() {
		return marker;
	}

}
