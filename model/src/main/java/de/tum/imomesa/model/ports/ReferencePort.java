package de.tum.imomesa.model.ports;

import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.expressions.Expression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

public class ReferencePort extends Port {
	
	// Constructors
	
	public ReferencePort() {
		super();
	}
	
	public ReferencePort(ReferenceComponent parent, DefinitionPort port) {
		setParent(parent);
		setPortImplementation(port);
	}
	
	// Name
	
	@Override
	@Transient
	public StringProperty nameProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().nameProperty();
		}
		throw new IllegalStateException();
	}
	
	// Description
	
	@Override
	@Transient
	public StringProperty descriptionProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().descriptionProperty();
		}
		throw new IllegalStateException();
	}
	
	// Direction
	
	@Override
	@Transient
	public IntegerProperty directionProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().directionProperty();
		}
		throw new IllegalStateException();
	}
	
	// Read type
	
	@Override
	@Transient
	public ObjectProperty<Class<?>> readTypeProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().readTypeProperty();
		}
		throw new IllegalStateException();
	}
	
	// Write types
	
	@Override
	@Transient
	public ListProperty<Class<?>> writeTypeProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().writeTypeProperty();
		}
		throw new IllegalStateException();
	}
	
	// Default (expression)
	
	@Override
	@Transient
	public ObjectProperty<Expression> defaultProperty() {
		if (getPortImplementation() != null) {
			return getPortImplementation().defaultProperty();
		}
		throw new IllegalStateException();
	}
	
	// Port implementation
	
	private ObjectProperty<DefinitionPort> portImplementation = new SimpleObjectProperty<DefinitionPort>();
	
	public DefinitionPort getPortImplementation() {
		return portImplementationProperty().get();
	}
	public void setPortImplementation(DefinitionPort value) {
		this.portImplementationProperty().set(value);
	}
	public ObjectProperty<DefinitionPort> portImplementationProperty() {
		return portImplementation;
	}
	
}