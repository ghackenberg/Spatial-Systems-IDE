package de.tum.imomesa.model.volumes;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;

public class CompositeVolume extends Volume
{
	// ########################################################################
	// Constructors
	// ########################################################################
	
	
	// ########################################################################
	// Methods
	// ########################################################################

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for(Volume v : volumes) {
			v.accept(visitor);
		}
	}

	// ########################################################################
	// Properties
	// ########################################################################
	
	// Volumes
	private final ListProperty<Volume> volumes = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public List<Volume> getVolumes()
	{
		return volumes.get();
	}
	public void setVolumes(List<Volume> volumes)
	{
		this.volumes.set(FXCollections.observableList(volumes));
	}
	@Cascading
	public ListProperty<Volume> volumesProperty()
	{
		return volumes;
	}
}
