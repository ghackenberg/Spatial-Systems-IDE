package de.tum.imomesa.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class TypedElement extends Element
{
	
	public TypedElement()
	{
		this(Object.class);
	}
	public TypedElement(Class<?> type)
	{
		setType(type);
	}
	
	// TypeObject
	private ObjectProperty<Class<?>> type = new SimpleObjectProperty<>();
	
	public Class<?> getType()
	{
		return type.get();
	}
	public void setType(Class<?> type)
	{
		this.type.set(type);
	}
	public ObjectProperty<Class<?>> typeProperty()
	{
		return type;
	}
	
}
