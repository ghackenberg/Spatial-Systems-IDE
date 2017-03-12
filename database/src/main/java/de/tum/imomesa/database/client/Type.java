package de.tum.imomesa.database.client;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.database.annotations.Transient;
import javafx.beans.property.Property;

public class Type {

	private Class<?> type;
	private List<Field> fields = new ArrayList<>();
	
	public Type(Class<?> type) {
		this.type = type;
		// Iterate over methods
		for (Method method : type.getMethods()) {
			// Filter property getters
			if (method.getParameters().length == 0 && Property.class.isAssignableFrom(method.getReturnType()) && !method.isAnnotationPresent(Transient.class) && method.getName().endsWith("Property")) {
				// Create field object
				Field field = new Field(method);
				// Register field object
				fields.add(field);
			}
		}
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public String getName() {
		return type.getName();
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public boolean hasField(String name) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public Field getField(String name) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		throw new IllegalStateException(name);
	}
	
	public List<Field> getCascadingFields() {
		List<Field> result = new ArrayList<>();
		for (Field field : fields) {
			if (field.isCascading()) {
				result.add(field);
			}
		}
		return result;
	}
	
}
