package de.tum.imomesa.model.expressions;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class UnaryExpression extends CompositeExpression {

	public UnaryExpression() {
		super();
	}

	public UnaryExpression(Class<?> typeObject, Class<?> argumentTypeObject) {
		super(typeObject, argumentTypeObject);
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getArgument() != null) {
			getArgument().accept(visitor);
		}
	}

	// Argument
	private ObjectProperty<Expression> argument = new SimpleObjectProperty<>();

	public Expression getArgument() {
		return argument.get();
	}

	public void setArgument(Expression argument) {
		this.argument.set(argument);
	}

	@Cascading
	public ObjectProperty<Expression> argumentProperty() {
		return argument;
	}

}
