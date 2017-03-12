package de.tum.imomesa.database.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Hobby {

	private StringProperty name = new SimpleStringProperty("My Hobby");
	
	public void setName(String name) {
		this.name.set(name);
	}
	public String getName() {
		return name.get();
	}
	public StringProperty nameProperty() {
		return name;
	}
	
}
