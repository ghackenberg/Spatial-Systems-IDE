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
		// create string property
		StringExpression s = new SimpleStringProperty("Random");

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Random";
	}

}
