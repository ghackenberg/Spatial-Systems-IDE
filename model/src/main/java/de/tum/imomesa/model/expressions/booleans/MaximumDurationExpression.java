package de.tum.imomesa.model.expressions.booleans;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class MaximumDurationExpression extends AtomicExpression {

	public MaximumDurationExpression() {
		super(Boolean.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		return new SimpleStringProperty("MaximumDuration");
	}

	@Override
	public String toString() {
		return "MaximumDuration";
	}

	// Limit
	private DoubleProperty limit = new SimpleDoubleProperty(0.);

	public Double getLimit() {
		return limit.get();
	}

	public void setLimit(Double value) {
		this.limit.set(value);
	}

	public DoubleProperty limitProperty() {
		return limit;
	}

}
