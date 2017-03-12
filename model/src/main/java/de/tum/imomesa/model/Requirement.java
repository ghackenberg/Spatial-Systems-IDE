package de.tum.imomesa.model;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class Requirement extends NamedElement {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Requirement r : requirements) {
			r.accept(visitor);
		}
	}

	// Requirements
	private final ListProperty<Requirement> requirements = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<Requirement> getRequirements() {
		return requirements.get();
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements.set(FXCollections.observableList(requirements));
	}

	@Cascading
	public ListProperty<Requirement> requirementsProperty() {
		return requirements;
	}

}
