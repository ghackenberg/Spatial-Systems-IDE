package de.tum.imomesa.model;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.utilities.visitors.Visitor;
import eu.mihosoft.jcsg.CSG;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class Part extends NamedElement {
	
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for(Transform t : transforms) {
			t.accept(visitor);
		}
		if(getVolume() != null) {
			getVolume().accept(visitor);
		}
	}

	// Red
	private IntegerProperty red = new SimpleIntegerProperty();
	
	public Integer getRed()
	{
		return red.get();
	}
	public void setRed(int red)
	{
		this.red.set(red);
	}
	public IntegerProperty redProperty()
	{
		return red;
	}

	// Green
	private IntegerProperty green = new SimpleIntegerProperty();
	
	public Integer getGreen()
	{
		return green.get();
	}
	public void setGreen(int green)
	{
		this.green.set(green);
	}
	public IntegerProperty greenProperty()
	{
		return green;
	}

	// Blue
	private IntegerProperty blue = new SimpleIntegerProperty();
	
	public Integer getBlue()
	{
		return blue.get();
	}
	public void setBlue(int blue)
	{
		this.blue.set(blue);
	}
	public IntegerProperty blueProperty()
	{
		return blue;
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
	
	public CSG toCSG() {
		return getVolume().getCSG().transformed(Transform.toVVecMathTransform(getTransforms()));
	}
	
}
