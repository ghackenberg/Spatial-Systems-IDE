package de.tum.imomesa.model.expressions.booleans;

import de.tum.imomesa.model.expressions.UnaryExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class NotExpression extends UnaryExpression {

	public NotExpression() {
		super(Boolean.class, Boolean.class);
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
		s = Bindings.concat("Not(", s, ")");

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Not";
	}

}
