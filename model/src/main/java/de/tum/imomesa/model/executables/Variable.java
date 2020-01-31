package de.tum.imomesa.model.executables;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Variable extends Observation {

	public Variable() {
		this(Object.class);
	}

	public Variable(Class<?> typeObject) {
		setReadType(typeObject);
		getWriteType().add(typeObject);
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
