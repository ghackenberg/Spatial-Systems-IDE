package de.tum.imomesa.model.expressions;

import de.tum.imomesa.database.annotations.Transient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class CompositeExpression extends Expression {

	public CompositeExpression() {
		super();

		setArgumentType(Object.class);
	}

	public CompositeExpression(Class<?> typeObject, Class<?> argumentTypeObject) {
		super(typeObject);

		setArgumentType(argumentTypeObject);
	}

	// ArgumentTypeObject
	private ObjectProperty<Class<?>> argumentType = new SimpleObjectProperty<>();

	public Class<?> getArgumentType() {
		return argumentType.get();
	}

	public void setArgumentType(Class<?> argumentTypeObject) {
		this.argumentType.set(argumentTypeObject);
	}

	@Transient
	public ObjectProperty<Class<?>> argumentTypeProperty() {
		return argumentType;
	}

}
