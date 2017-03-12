package de.tum.imomesa.model.expressions;

import javafx.beans.binding.StringExpression;
import de.tum.imomesa.model.TypedElement;

public abstract class Expression extends TypedElement {

	public Expression() {
		super();
	}

	public Expression(Class<?> typeObject) {
		super(typeObject);
	}

	public abstract StringExpression toStringExpression();

}
