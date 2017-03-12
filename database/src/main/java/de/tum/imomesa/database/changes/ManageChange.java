package de.tum.imomesa.database.changes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class ManageChange extends Change {

	public ManageChange(double client, double key, long timestamp, Class<?> type) {
		super(client, key, timestamp, "Object managed (type = " + type.getName() + ").");
		
		this.type = new ReadOnlyObjectWrapper<>(type);
	}
	
	private ObjectProperty<Class<?>> type;
	
	public Class<?> getType() {
		return type.get();
	}
	
	public ObjectProperty<Class<?>> typeProperty() {
		return type;
	}

}
