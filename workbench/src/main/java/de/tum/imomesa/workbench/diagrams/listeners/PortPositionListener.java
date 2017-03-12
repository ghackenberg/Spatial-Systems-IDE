package de.tum.imomesa.workbench.diagrams.listeners;

import javafx.beans.binding.NumberExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Line;

public class PortPositionListener implements ChangeListener<Number> {

	private NumberExpression startX;
	private NumberExpression startY;
	private NumberExpression endX;
	private NumberExpression endY;
	private NumberExpression radius;
	private Line line;

	public PortPositionListener(NumberExpression startX, NumberExpression startY, NumberExpression endX,
			NumberExpression endY, NumberExpression radius, Line line) {

		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.radius = radius;
		this.line = line;

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		update();
	}

	private void update() {
		double dx = endX.getValue().doubleValue() - startX.getValue().doubleValue();
		double dy = endY.getValue().doubleValue() - startY.getValue().doubleValue();

		double length = Math.sqrt(dx * dx + dy * dy);

		dx /= length;
		dy /= length;

		line.setStartX(startX.getValue().doubleValue() + dx * radius.getValue().doubleValue());
		line.setStartY(startY.getValue().doubleValue() + dy * radius.getValue().doubleValue());

		line.setEndX(endX.getValue().doubleValue() - dx * radius.getValue().doubleValue());
		line.setEndY(endY.getValue().doubleValue() - dy * radius.getValue().doubleValue());
	}

}
