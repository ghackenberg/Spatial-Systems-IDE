package de.tum.imomesa.workbench.diagrams.listeners;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public class RectanglePositionListener implements ChangeListener<Number> {

	private Rectangle rectangle;
	private Group group;
	private DoubleProperty angle;

	public RectanglePositionListener(Rectangle rectangle, Group group, DoubleProperty angle) {
		this.rectangle = rectangle;
		this.group = group;
		this.angle = angle;

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		update();
	}

	private void update() {
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();
		
		// TODO Not really an angle!
		double angle = ((this.angle.get() + 360.) % 360.) / 360.;
		
		double dx = 0;
		double dy = 0;
		
		if (angle < 0.25) {
			dx = 1;
			dy = (angle - 0.) / 0.25;
		} else if (angle < 0.5) {
			dx = 1 - (angle - 0.25) / 0.25;
			dy = 1;
		} else if (angle < 0.75) {
			dx = 0;
			dy = 1 - (angle - 0.5) / 0.25;
		} else if (angle < 1.) {
			dx = (angle - 0.75) / 0.25;
			dy = 0;
		} else {
			throw new IllegalStateException();
		}
		
		group.setTranslateX(rectangle.getX() + width * dx);
		group.setTranslateY(rectangle.getY() + height * dy);
	}

}
