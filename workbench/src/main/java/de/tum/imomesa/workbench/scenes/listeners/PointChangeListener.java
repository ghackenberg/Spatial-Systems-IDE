package de.tum.imomesa.workbench.scenes.listeners;

import de.tum.imomesa.model.commons.Point;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;

public class PointChangeListener implements ChangeListener<Number> {
	
	// data
	private ObjectProperty<Point3D> data;
	private ObjectProperty<Point> original;

	// constructor
	public PointChangeListener(ObjectProperty<Point3D> data, ObjectProperty<Point> original) {
		this.data = data;
		this.original = original;
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		data.set(new Point3D(original.get().getX(), original.get().getY(), original.get().getZ()));
	}

}
