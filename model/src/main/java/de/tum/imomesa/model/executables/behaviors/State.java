package de.tum.imomesa.model.executables.behaviors;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class State extends Label {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Part p : parts) {
			p.accept(visitor);
		}
	}

	// Parts
	private ListProperty<Part> parts = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Part> getParts() {
		return parts.get();
	}

	public void setParts(List<Part> parts) {
		this.parts.set(FXCollections.observableList(parts));
	}

	@Cascading
	public ListProperty<Part> partsProperty() {
		return parts;
	}
	
}
