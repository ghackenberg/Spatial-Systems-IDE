package de.tum.imomesa.model.volumes;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class BoxVolume extends AtomicVolume {
	
	public BoxVolume() {
		width.addListener(this::updateCSG);
		height.addListener(this::updateCSG);
		depth.addListener(this::updateCSG);
	}
	
	// Width
	private DoubleProperty width = new SimpleDoubleProperty(0);
	
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
	private DoubleProperty height = new SimpleDoubleProperty(0);
	
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
	private DoubleProperty depth = new SimpleDoubleProperty(0);
	
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
	
	protected CSG toCSG() {
		return new Cube(getWidth(), getHeight(), getDepth()).toCSG().transformed(getTransform());
	}
	
}
