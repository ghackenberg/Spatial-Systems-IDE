package de.tum.imomesa.model.expressions;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class TerniaryExpression extends CompositeExpression {

	public TerniaryExpression() {
		super();

		try {
			setArgumentOneType(Object.class);
			setArgumentTwoType(Object.class);
			setArgumentThreeType(Object.class);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public TerniaryExpression(Class<?> typeObject, Class<?> argumentTypeObject1, Class<?> argumentTypeObject2,
			Class<?> argumentTypeObject3) {
		super(typeObject, argumentTypeObject1);

		try {
			setArgumentOneType(argumentTypeObject1);
			setArgumentTwoType(argumentTypeObject2);
			setArgumentThreeType(argumentTypeObject3);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getArgumentOne() != null) {
			getArgumentOne().accept(visitor);
		}
		if (getArgumentTwo() != null) {
			getArgumentTwo().accept(visitor);
		}
		if (getArgumentThree() != null) {
			getArgumentThree().accept(visitor);
		}
	}

	// ArgumentExpression 1
	private ObjectProperty<Expression> argumentOne = new SimpleObjectProperty<>();

	public Expression getArgumentOne() {
		return argumentOne.get();
	}

	public void setArgumentOne(Expression argument) {
		this.argumentOne.set(argument);
	}

	@Cascading
	public ObjectProperty<Expression> argumentOneProperty() {
		return argumentOne;
	}

	// ArgumentTypeObject 1
	private ObjectProperty<Class<?>> argumentOneType = new SimpleObjectProperty<>();

	public Class<?> getArgumentOneType() {
		return argumentOneType.get();
	}

	public void setArgumentOneType(Class<?> argumentTypeObject) throws ClassNotFoundException {
		this.argumentOneType.set(argumentTypeObject);
	}

	@Transient
	public ObjectProperty<Class<?>> argumentOneTypeProperty() {
		return argumentOneType;
	}

	// ArgumentExpression 2
	private ObjectProperty<Expression> argumentTwo = new SimpleObjectProperty<>();

	public Expression getArgumentTwo() {
		return argumentTwo.get();
	}

	public void setArgumentTwo(Expression argument) {
		this.argumentTwo.set(argument);
	}

	@Cascading
	public ObjectProperty<Expression> argumentTwoProperty() {
		return argumentTwo;
	}

	// ArgumentTypeObject 2
	private ObjectProperty<Class<?>> argumentTwoType = new SimpleObjectProperty<>();

	public Class<?> getArgumentTwoType() {
		return argumentTwoType.get();
	}

	public void setArgumentTwoType(Class<?> argumentTypeObject) {
		this.argumentTwoType.set(argumentTypeObject);
	}

	@Transient
	public ObjectProperty<Class<?>> argumentTwoTypeProperty() {
		return argumentTwoType;
	}

	// ArgumentExpression 3
	private ObjectProperty<Expression> argumentThree = new SimpleObjectProperty<>();

	public Expression getArgumentThree() {
		return argumentThree.get();
	}

	public void setArgumentThree(Expression argument) {
		this.argumentThree.set(argument);
	}

	@Cascading
	public ObjectProperty<Expression> argumentThreeProperty() {
		return argumentThree;
	}

	// ArgumentTypeObject 3
	private ObjectProperty<Class<?>> argumentThreeType = new SimpleObjectProperty<>();

	public Class<?> getArgumentThreeType() {
		return argumentThreeType.get();
	}

	public void setArgumentThreeType(Class<?> argumentTypeObject) {
		this.argumentThreeType.set(argumentTypeObject);
	}

	@Transient
	public ObjectProperty<Class<?>> argumentThreeTypeProperty() {
		return argumentThreeType;
	}

}
