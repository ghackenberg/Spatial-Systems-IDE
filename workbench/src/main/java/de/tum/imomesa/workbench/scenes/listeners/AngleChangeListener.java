package de.tum.imomesa.workbench.scenes.listeners;

import de.tum.imomesa.model.commons.Point;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public class AngleChangeListener implements ChangeListener<Number> {

	// data
	private Rotate target;
	private Point p;
	private Point axe;
	
	public AngleChangeListener(Rotate target, Point p, Point axe) {
		this.target = target;
		this.p = p;
		this.axe = axe;
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
		Point rotationAxe = p.crossProduct(axe);
		
		target.setAxis(new Point3D(rotationAxe.getX(), rotationAxe.getY(), rotationAxe.getZ()));
		double angle = p.getAngleDegree(axe);
		target.setAngle(-angle);
	}	
}
