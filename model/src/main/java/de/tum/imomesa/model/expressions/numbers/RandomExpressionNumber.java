package de.tum.imomesa.model.expressions.numbers;

import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class RandomExpressionNumber extends AtomicExpression {

	public RandomExpressionNumber() {
		super(Number.class);
	}

	@Override
	public StringExpression toStringExpression() {
		return new SimpleStringProperty("RandomNumber");
	}

	@Override
	public String toString() {
		return "RandomNumber";
	}

}
