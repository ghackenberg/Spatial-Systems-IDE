package de.tum.imomesa.database.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.tum.imomesa.database.annotations.Cascading;
import javafx.beans.property.Property;

public class Field {
	
	private Method method;
	
	public Field(Method method) {
		this.method = method;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public String getName() {
		return method.getName().substring(0, method.getName().length() - "Property".length());
	}
	
	@SuppressWarnings("unchecked")
	public Property<Object> getValue(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Property<Object>) method.invoke(instance);
	}
	
	public boolean isCascading() {
		return method.isAnnotationPresent(Cascading.class);
	}
	
}
