package de.tum.imomesa.model.executables;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Guard extends Element {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getExpression() != null) {
			getExpression().accept(visitor);
		}
	}

	// Expression
	private ObjectProperty<Expression> expression = new SimpleObjectProperty<>();

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
