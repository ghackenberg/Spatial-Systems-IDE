package de.tum.imomesa.model.volumes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BoxVolume extends AtomicVolume
{
	// ########################################################################
	// Constructors
	// ########################################################################
	
	public BoxVolume() {
		super();
		
		setWidth(0.);
		setHeight(0.);
		setDepth(0.);
	}
	
	// ########################################################################
	// Methods
	// ########################################################################
	
	
	// ########################################################################
	// Properties
	// ########################################################################
	
	// Width
	private DoubleProperty width = new SimpleDoubleProperty();
	
	public Double getWidth()
	{
		return width.get();
	}
	public void setWidth(Double width)
	{
		this.width.set(width);
	}
	public DoubleProperty widthProperty()
	{
		return width;
	}
	
	// Height
	private DoubleProperty height = new SimpleDoubleProperty();
	
	public Double getHeight()
	{
		return height.get();
	}
	public void setHeight(Double height)
	{
		this.height.set(height);
	}
	public DoubleProperty heightProperty()
	{
		return height;
	}
	
	// Depth
	private DoubleProperty depth = new SimpleDoubleProperty();
	
	public Double getDepth()
	{
		return depth.get();
	}
	public void setDepth(Double depth)
	{
		this.depth.set(depth);
	}
	public DoubleProperty depthProperty()
	{
		return depth;
	}
}
