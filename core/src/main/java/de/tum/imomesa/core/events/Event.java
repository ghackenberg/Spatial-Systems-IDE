package de.tum.imomesa.core.events;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public abstract class Event {
	
	private LongProperty timestamp = new SimpleLongProperty(System.currentTimeMillis());
	
	public Long getTimestamp() {
		return timestamp.get();
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp.set(timestamp);
	}
	
	public LongProperty timestampProperty() {
		return timestamp;
	}

}
