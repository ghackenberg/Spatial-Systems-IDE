package de.tum.imomesa.model.volumes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class AtomicVolume extends Volume
{
	// ########################################################################
	// Constructors
	// ########################################################################
	
	
	// ########################################################################
	// Methods
	// ########################################################################
	
	
	// ########################################################################
	// Properties
	// ########################################################################

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
}
