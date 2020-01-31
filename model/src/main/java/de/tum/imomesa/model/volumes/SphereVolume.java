package de.tum.imomesa.model.volumes;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Sphere;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SphereVolume extends AtomicVolume {

	public SphereVolume() {
		radius.addListener(this::updateCSG);
		slices.addListener(this::updateCSG);
		stacks.addListener(this::updateCSG);
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

	// Stacks
	private final IntegerProperty stacks = new SimpleIntegerProperty(20);

	public int getStacks() {
		return stacks.get();
	}

	public void setStacks(int stacks) {
		this.stacks.set(stacks);
	}

	public IntegerProperty stacksProperty() {
		return stacks;
	}

	protected CSG toCSG() {
		return new Sphere(getRadius(), getSlices(), getStacks()).toCSG().transformed(getTransform());
	}

}
