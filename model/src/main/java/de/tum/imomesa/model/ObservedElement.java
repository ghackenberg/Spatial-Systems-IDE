package de.tum.imomesa.model;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public abstract class ObservedElement extends NamedElement
{

	@Override
	public String toString()
	{
		if(getName() == null || getName().equals("")) {
			return this.getClass().getSimpleName();
		}
		return getName();
	}
	
	// WriteType
	
	private ListProperty<Class<?>> writeType = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public List<Class<?>> getWriteType()
	{
		return writeTypeProperty().get();
	}
	public void setWriteType(List<Class<?>> writeType)
	{
		writeTypeProperty().set(FXCollections.observableList(writeType));
	}
	public ListProperty<Class<?>> writeTypeProperty()
	{
		return writeType;
	}

	// ReadType
	
	private ObjectProperty<Class<?>> readType = new SimpleObjectProperty<>();
	
	public Class<?> getReadType()
	{
		return readTypeProperty().get();
	}
	public void setReadType(Class<?> readType)
	{
		readTypeProperty().set(readType);
	}
	public ObjectProperty<Class<?>> readTypeProperty()
	{
		return readType;
	}
	
}
