package de.tum.imomesa.model.expressions.numbers;

import java.util.Set;

import de.tum.imomesa.model.expressions.UnaryExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class CardinalityExpression extends UnaryExpression {

	public CardinalityExpression() {
		super(Number.class, Set.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		StringExpression s = new SimpleStringProperty("");

		// add inside argument
		if (getArgument() != null) {
			s = Bindings.concat(s, getArgument().toStringExpression());
		}

		// add specific data
		s = Bindings.concat("Cardinality(", s, ")");

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Cardinality";
	}

}
