package de.tum.imomesa.simulator.markers.warnings;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.simulator.markers.WarningMarker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class NegativeDelayWarning extends WarningMarker {

	public NegativeDelayWarning() {

	}

	public NegativeDelayWarning(List<Element> context, ObservationExpression exp, int step, int delay) {
		super(context, "Negative delay = " + delay, step);

		this.delay.set(delay);
	}

	private IntegerProperty delay = new SimpleIntegerProperty();

	public IntegerProperty delayProperty() {
		return delay;
	}

}
