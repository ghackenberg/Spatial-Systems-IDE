package de.tum.imomesa.model.expressions.booleans;

import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class EqualExpression extends NaryExpression {

	public EqualExpression() {
		super(Boolean.class, Object.class);
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		StringExpression s = new SimpleStringProperty("");

		// add inside arguments
		for (Expression e : getArguments()) {
			s = Bindings.concat(s, e.toStringExpression());

			// add comma if not last
			if (getArguments().indexOf(e) != getArguments().size() - 1) {
				s = Bindings.concat(s, ",");
			}
		}

		// add specific data
		s = Bindings.concat("Equal(", s, ")");

		// return result
		return s;
	}

	@Override
	public String toString() {
		return "Equal";
	}

}
