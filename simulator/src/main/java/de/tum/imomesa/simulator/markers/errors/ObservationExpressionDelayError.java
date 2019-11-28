package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ObservationExpressionDelayError extends ErrorMarker {

	public ObservationExpressionDelayError() {
		
	}
	
	public ObservationExpressionDelayError(List<Element> context, int step, int delay) {
		super(context, "Invalid observation expression delay = " + delay, step);
		
		this.delay.set(delay);
	}
	
	private IntegerProperty delay = new SimpleIntegerProperty();
	
	public IntegerProperty delayProperty() {
		return delay;
	}
	
	public int getDelay() {
		return delay.get();
	}

}
