package de.tum.imomesa.model.ports;

import java.util.Set;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class MaterialPort extends DefinitionPort
{
	
	// Constructors
	
	public MaterialPort()
	{
		setReadType(Set.class);
		getWriteType().add(Boolean.class);
		
		setDirection(Direction.INPUT_OUTPUT.ordinal());
	}
	
	// Methods
	
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if(getVolume() != null) {
			getVolume().accept(visitor);
		}
	}

	// Volume
	
	private ObjectProperty<Volume> volume = new SimpleObjectProperty<>();
	
	public Volume getVolume()
	{
		return volume.get();
	}
	public void setVolume(Volume volume)
	{
		this.volume.set(volume);
	}
	@Cascading
	public ObjectProperty<Volume> volumeProperty()
	{
		return volume;
	}

}
