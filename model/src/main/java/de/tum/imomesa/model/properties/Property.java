package de.tum.imomesa.model.properties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.utilities.visitors.Visitor;

public abstract class Property extends Observation {

	public Property() {
		super();
	}

	public Property(Class<?> typeObject) {
		setReadType(typeObject);
		getWriteType().add(typeObject);
	}

	@Override
	public String toString() {
		if (nameProperty().getValue() == null) {
			return this.getClass().getSimpleName();
		} else {
			return nameProperty().getValue();
		}
	}

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getExpression() != null) {
			getExpression().accept(visitor);
		}
	}

	// Expression
	private final ObjectProperty<Expression> expression = new SimpleObjectProperty<>();

	public Expression getExpression() {
		return expression.get();
	}

	public void setExpression(Expression expression) {
		this.expression.set(expression);
	}

	@Cascading
	public ObjectProperty<Expression> expressionProperty() {
		return expression;
	}
}
