package de.tum.imomesa.model.channels;

import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.ports.Port;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Channel extends NamedElement
{
	// ########################################################################
	// Constructors
	// ########################################################################
	
	// ########################################################################
	// Methods
	// ########################################################################
	
	// ########################################################################
	// Properties
	// ########################################################################
	
	// Source
	private ObjectProperty<Port> source = new SimpleObjectProperty<>();
	
	public Port getSource()
	{
		return source.get();
	}
	public void setSource(Port source)
	{
		this.source.set(source);
	}
	public ObjectProperty<Port> sourceProperty()
	{
		return source;
	}
	
	// Target
	private ObjectProperty<Port> target = new SimpleObjectProperty<>();
	
	public Port getTarget()
	{
		return target.get();
	}
	public void setTarget(Port target)
	{
		this.target.set(target);
	}
	public ObjectProperty<Port> targetProperty()
	{
		return target;
	}
	
}
