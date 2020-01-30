package de.tum.imomesa.model.volumes;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cylinder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CylinderVolume extends AtomicVolume {

	public CylinderVolume() {
		radius.addListener(this::updateCSG);
		height.addListener(this::updateCSG);
		slices.addListener(this::updateCSG);
	}

	// Radius
	private final DoubleProperty radius = new SimpleDoubleProperty(0);

	public double getRadius() {
		return radius.get();
	}

	public void setRadius(double radius) {
		this.radius.set(radius);
	}

	public DoubleProperty radiusProperty() {
		return radius;
	}

	// Height
	private final DoubleProperty height = new SimpleDoubleProperty(0);

	public double getHeight() {
		return height.get();
	}

	public void setHeight(double height) {
		this.height.set(height);
	}

	public DoubleProperty heightProperty() {
		return height;
	}

	// Slices
	private final IntegerProperty slices = new SimpleIntegerProperty(20);

	public int getSlices() {
		return slices.get();
	}
	
	public void setSlices(int slices) {
		this.slices.set(slices);
	}
	
	public IntegerProperty slicesProperty() {
		return slices;
	}

	protected CSG toCSG() {
		return new Cylinder(getRadius(), getHeight(), getSlices()).toCSG().transformed(getTransform());
	}

}
