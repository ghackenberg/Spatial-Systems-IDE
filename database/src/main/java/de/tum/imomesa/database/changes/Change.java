package de.tum.imomesa.database.changes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

public abstract class Change {
	
	public Change(double client, double key, long timestamp, String message) {
		this.client = new ReadOnlyDoubleWrapper(client);
		this.key = new ReadOnlyDoubleWrapper(key);
		this.timestamp = new ReadOnlyLongWrapper(timestamp);
		this.message = new ReadOnlyStringWrapper(message);
	}
	
	public StringProperty classProperty() {
		return new ReadOnlyStringWrapper(getClass().getSimpleName());
	}
	
	private DoubleProperty client;
	
	public Double getClient() {
		return client.get();
	}
	
	public DoubleProperty clientProperty() {
		return client;
	}
	
	private DoubleProperty key;
	
	public Double getKey() {
		return key.get();
	}
	
	public DoubleProperty keyProperty() {
		return key;
	}
	
	private LongProperty timestamp;
	
	public Long getTimestamp() {
		return timestamp.get();
	}
	
	public void setTimesamp(Long timestamp) {
		this.timestamp.set(timestamp);
	}
	
	public LongProperty timestampProperty() {
		return timestamp;
	}
	
	private ObjectProperty<Object> object = new SimpleObjectProperty<>();
	
	public Object getObject() {
		return object.get();
	}
	
	public void setObject(Object object) {
		this.object.set(object);
	}
	
	public ObjectProperty<Object> objectProperty() {
		return object;
	}
	
	private StringProperty message;
	
	public String getMessage() {
		return message.get();
	}
	
	public void setMessage(String message) {
		this.message.set(message);
	}
	
	public StringProperty messageProperty() {
		return message;
	}
	
}