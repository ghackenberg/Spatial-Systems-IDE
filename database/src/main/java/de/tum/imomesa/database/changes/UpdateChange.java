package de.tum.imomesa.database.changes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

public class UpdateChange extends Change {
	
	private StringProperty name;
	private ObjectProperty<Object> value;
	
	public UpdateChange(double client, double key, long timestamp, String name, Object value) {
		super(client, key, timestamp, "Object updated (" + name + " = " + value + ").");
		
		this.name = new ReadOnlyStringWrapper(name);
		this.value = new ReadOnlyObjectWrapper<>(value);
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	public ObjectProperty<Object> valueProperty() {
		return value;
	}

}
