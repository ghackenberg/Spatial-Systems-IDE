package de.tum.imomesa.model.expressions.booleans;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ConstantExpressionBoolean extends AtomicExpression {

	public ConstantExpressionBoolean() {
		super(Boolean.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		StringExpression s = new SimpleStringProperty("");

		// add specific data
		s = Bindings.concat(value);

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Constant (Boolean)";
	}

	// Value
	private BooleanProperty value = new SimpleBooleanProperty();

	public Boolean getValue() {
		return value.get();
	}

	public void setValue(Boolean value) {
		this.value.set(value);
	}

	public BooleanProperty valueProperty() {
		return value;
	}

}
