package de.tum.imomesa.model.expressions;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class NullExpression extends AtomicExpression {
	
	public NullExpression() {
		super(Object.class);
	}

	@Override
	public StringExpression toStringExpression() {
		return new SimpleStringProperty("Null");
	}
	
	@Override
	public String toString() {
		return "Null ( " + getType().getSimpleName() + " )";
	}

}
