package de.tum.imomesa.workbench.diagrams.listeners;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Line;

public class LineAngleListener implements ChangeListener<Number> {

	private Line line;
	private DoubleProperty angle;
	
	public LineAngleListener(Line line, DoubleProperty angle) {
		this.line = line;
		this.angle = angle;
		
		update();
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		update();
	}
	
	private void update() {
		double dx = line.getEndX() - line.getStartX();
		double dy = line.getEndY() - line.getStartY();
		
		double angle = Math.atan2(dy, dx);
		
		// System.out.println(dx + " vs " + dy + " = " + angle / Math.PI * 180);
		
		this.angle.set(angle / Math.PI * 180);
	}
	
}
