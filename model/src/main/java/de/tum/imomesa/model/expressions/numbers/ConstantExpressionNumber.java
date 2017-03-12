package de.tum.imomesa.model.expressions.numbers;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class ConstantExpressionNumber extends AtomicExpression {

	public ConstantExpressionNumber() {
		super(Number.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		StringExpression s = new SimpleStringProperty("");

		// add specific data
		s = Bindings.concat(doubleValue);

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Constant (Number)";
	}

	// Value
	private DoubleProperty doubleValue = new SimpleDoubleProperty();

	public Double getDoubleValue() {
		return doubleValue.get();
	}

	public void setDoubleValue(Double value) {
		this.doubleValue.set(value);
	}

	public DoubleProperty doubleValueProperty() {
		return doubleValue;
	}
}
