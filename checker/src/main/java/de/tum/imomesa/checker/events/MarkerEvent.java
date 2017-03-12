package de.tum.imomesa.checker.events;

import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.Event;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class MarkerEvent extends Event {

	public MarkerEvent() {
		
	}
	
	public MarkerEvent(SyntacticMarker marker) {
		this.marker.set(marker);
	}

	private ObjectProperty<SyntacticMarker> marker = new SimpleObjectProperty<>();

	public SyntacticMarker getMarker() {
		return marker.get();
	}
	
	public ObjectProperty<SyntacticMarker> markerProperty() {
		return marker;
	}
}
