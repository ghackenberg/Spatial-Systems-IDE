package de.tum.imomesa.model.volumes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class CylinderVolume extends AtomicVolume
{
	
	// ########################################################################
	// Constructors
	// ########################################################################
	
	public CylinderVolume() {
		super();
		
		setRadius(0);
		setHeight(0);
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

	// Height
	private DoubleProperty height = new SimpleDoubleProperty();
	
	public double getHeight()
	{
		return height.get();
	}
	public void setHeight(double height)
	{
		this.height.set(height);
	}
	public DoubleProperty heightProperty()
	{
		return height;
	}

}
