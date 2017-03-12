package de.tum.imomesa.workbench.scenes.listeners;

import de.tum.imomesa.model.commons.Point;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class LengthChangeListener implements ChangeListener<Number> {
	
	// data
	private Point originalPoint;
	private DoubleProperty target;
	
	public LengthChangeListener(Point originalPoint, DoubleProperty target) {
		this.originalPoint = originalPoint;
		this.target = target;
	}

	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
		target.setValue(originalPoint.getLenght());	
	}

}
