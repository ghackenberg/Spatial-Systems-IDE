package de.tum.imomesa.model;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.utilities.visitors.Visitor;

public class Part extends NamedElement
{
	// ########################################################################
	// Constructors
	// ########################################################################

	
	// ########################################################################
	// Methods
	// ########################################################################
	
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for(Transform t : transforms) {
			t.accept(visitor);
		}
		if(getVolume() != null) {
			getVolume().accept(visitor);
		}
	}

	
	// ########################################################################
	// Properties
	// ########################################################################

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
	
	// Transforms
	private final ListProperty<Transform> transforms = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Transform> getTransforms()
	{
		return transforms.get();
	}
	public void setTransforms(List<Transform> transforms)
	{
		this.transforms.set(FXCollections.observableList(transforms));
	}
	@Cascading
	public ListProperty<Transform> transformsProperty()
	{
		return transforms;
	}
}
