package de.tum.imomesa.model.ports;

import de.tum.imomesa.model.components.DefinitionComponent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class LifeMaterialPort extends MaterialPort
{
	
	// Component
	
	private ObjectProperty<DefinitionComponent> component = new SimpleObjectProperty<>();
	
	public DefinitionComponent getComponent()
	{
		return component.get();
	}
	public void setComponent(DefinitionComponent component)
	{
		this.component.set(component);
	}
	public ObjectProperty<DefinitionComponent> componentProperty()
	{
		return component;
	}

}