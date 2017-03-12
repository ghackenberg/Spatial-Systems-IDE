package de.tum.imomesa.analyzer.nodes.elements;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

public abstract class TreeElement {

	public TreeElement(String name) {
		setName(name);
	}

	private StringProperty name = new SimpleStringProperty();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	@Override
	public final String toString() {
		return getName();
	}

	public abstract Node toNode();

}
