package de.tum.imomesa.model;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;

public class Workspace extends NamedElement {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Project p : projects) {
			p.accept(visitor);
		}
	}

	// Projects
	private final ListProperty<Project> projects = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Project> getProjects() {
		return projects.get();
	}

	public void setProjects(List<Project> projects) {
		this.projects.set(FXCollections.observableList(projects));
	}

	@Cascading
	public ListProperty<Project> projectsProperty() {
		return projects;
	}
}
