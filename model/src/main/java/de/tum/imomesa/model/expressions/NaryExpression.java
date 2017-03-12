package de.tum.imomesa.model.expressions;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;

public abstract class NaryExpression extends CompositeExpression {

	public NaryExpression() {
		super();
	}

	public NaryExpression(Class<?> typeObject, Class<?> argumentTypeObject) {
		super(typeObject, argumentTypeObject);
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Expression e : arguments) {
			e.accept(visitor);
		}
	}

	// Arguments
	private ListProperty<Expression> arguments = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Expression> getArguments() {
		return arguments.get();
	}

	public void setArguments(List<Expression> arguments) {
		this.arguments.set(FXCollections.observableList(arguments));
	}

	@Cascading
	public ListProperty<Expression> argumentsProperty() {
		return arguments;
	}

}
