package de.tum.imomesa.model.expressions;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;

public class IfClauseExpression extends TerniaryExpression {

	public IfClauseExpression() {
		super(Object.class, Boolean.class, Object.class, Object.class);
	}

	public IfClauseExpression(Class<?> typeObject) {
		super(typeObject, Boolean.class, typeObject, typeObject);
	}

	@Override
	public void setType(Class<?> type) {
		super.setType(type);
		if (argumentTwoTypeProperty() != null) {
			setArgumentTwoType(type);
		}
		if (argumentThreeTypeProperty() != null) {
			setArgumentThreeType(type);
		}
	}

	@Override
	public StringExpression toStringExpression() {
		// create string property
		StringExpression s = new SimpleStringProperty("If(");
		if (getArgumentOne() != null) {
			s = Bindings.concat(s, getArgumentOne().toStringExpression());
		} else {
			s = Bindings.concat(s, "<>");
		}
		s = Bindings.concat(s, ",");
		if (getArgumentTwo() != null) {
			s = Bindings.concat(s, getArgumentTwo().toStringExpression());
		} else {
			s = Bindings.concat(s, "<>");
		}
		s = Bindings.concat(s, ",");
		if (getArgumentThree() != null) {
			s = Bindings.concat(s, getArgumentThree().toStringExpression());
		} else {
			s = Bindings.concat(s, "<>");
		}
		s = Bindings.concat(s, ")");
		return s;
	}

	@Override
	public String toString() {
		return "If ( " + getType().getSimpleName() + " )";
	}

}
