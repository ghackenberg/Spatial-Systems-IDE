package de.tum.imomesa.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class NamedElement extends Element {

	@Override
	public String toString() {
		if (getName() == null || getName().equals("")) {
			return this.getClass().getSimpleName();
		} else {
			return getName();
		}
	}

	// Name
	private final StringProperty name = new SimpleStringProperty();

	public String getName() {
		return nameProperty().get();
	}

	public void setName(String name) {
		this.nameProperty().set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	// Description
	private final StringProperty description = new SimpleStringProperty();

	public String getDescription() {
		return descriptionProperty().get();
	}

	public void setDescription(String description) {
		this.descriptionProperty().set(description);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

}
