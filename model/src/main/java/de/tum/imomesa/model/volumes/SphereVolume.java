package de.tum.imomesa.model.volumes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SphereVolume extends AtomicVolume
{
	
	// ########################################################################
	// Constructors
	// ########################################################################
	
	public SphereVolume() {
		super();
		
		setRadius(0);
	}
	
	// ########################################################################
	// Methods
	// ########################################################################
	
	
	// ########################################################################
	// Properties
	// ########################################################################
	
	// Radius
	private DoubleProperty radius = new SimpleDoubleProperty();
	
	public double getRadius()
	{
		return radius.get();
	}
	public void setRadius(double radius)
	{
		this.radius.set(radius);
	}
	public DoubleProperty radiusProperty()
	{
		return radius;
	}
}
