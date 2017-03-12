package de.tum.imomesa.model.expressions.numbers;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class DurationExpression extends AtomicExpression {

	public DurationExpression() {
		super(Number.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		return new SimpleStringProperty("Duration");
	}

	@Override
	public String toString() {
		return "Duration";
	}

}
