package de.tum.imomesa.model.expressions.booleans;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class RandomExpressionBoolean extends AtomicExpression {

	public RandomExpressionBoolean() {
		super(Boolean.class);
	}

	@Override
	public StringExpression toStringExpression() {
		return new SimpleStringProperty("RandomBoolean");
	}

	@Override
	public String toString() {
		return "RandomBoolean";
	}

}
