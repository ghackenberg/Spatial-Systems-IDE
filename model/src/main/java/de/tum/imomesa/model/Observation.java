package de.tum.imomesa.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.utilities.visitors.Visitor;

public abstract class Observation extends ObservedElement {
	
	// Constructors
	
	public Observation() {
		super();
	}
	
	// Methods
	
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if(getDefault() != null) {
			getDefault().accept(visitor);
		}
	}
	
	// Default
	
	private ObjectProperty<Expression> _default = new SimpleObjectProperty<>();
	
	public Expression getDefault() {
		return defaultProperty().get();
	}
	public void setDefault(Expression _default) {
		defaultProperty().set(_default);
	}
	@Cascading
	public ObjectProperty<Expression> defaultProperty() {
		return _default;
	}

}
