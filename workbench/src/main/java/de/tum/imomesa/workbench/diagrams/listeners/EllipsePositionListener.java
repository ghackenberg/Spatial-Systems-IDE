package de.tum.imomesa.workbench.diagrams.listeners;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.shape.Ellipse;

public class EllipsePositionListener implements ChangeListener<Number> {

	private Ellipse ellipse;
	private Group group;
	private DoubleProperty angle;

	public EllipsePositionListener(Ellipse ellipse, Group group, DoubleProperty angle) {
		this.ellipse = ellipse;
		this.group = group;
		this.angle = angle;

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		update();
	}

	private void update() {
		double width = ellipse.getRadiusX();
		double height = ellipse.getRadiusY();

		double dx = Math.cos(angle.get() / 180 * Math.PI);
		double dy = Math.sin(angle.get() / 180 * Math.PI);

		group.setTranslateX(ellipse.getCenterX() + width * dx);
		group.setTranslateY(ellipse.getCenterY() + height * dy);
	}

}
