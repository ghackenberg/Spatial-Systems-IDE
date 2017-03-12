package de.tum.imomesa.model.ports;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;

public class KinematicEnergyPort extends EnergyPort
{
	
	// Constructors
	
	public KinematicEnergyPort()
	{
		setReadType(RealMatrix.class);
		
		getWriteType().add(Number.class);
		getWriteType().add(RealMatrix.class);
	}
	
	// Methods
	
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if(getTransform() != null) {
			getTransform().accept(visitor);
		}
	}
	
	// Transform
	
	private ObjectProperty<Transform> transform = new SimpleObjectProperty<>();
	
	public Transform getTransform()
	{
		return transform.get();
	}
	public void setTransform(Transform transform)
	{
		this.transform.set(transform);
	}
	@Cascading
	public ObjectProperty<Transform> transformProperty()
	{
		return transform;
	}
	
}
