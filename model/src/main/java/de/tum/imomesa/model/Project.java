package de.tum.imomesa.model;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class Project extends NamedElement {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (DefinitionComponent c : templates) {
			c.accept(visitor);
		}
		if (getComponent() != null) {
			getComponent().accept(visitor);
		}
	}

	// Component
	private final ObjectProperty<DefinitionComponent> component = new SimpleObjectProperty<>();

	public DefinitionComponent getComponent() {
		return component.get();
	}

	public void setComponent(DefinitionComponent component) {
		this.component.set(component);
	}

	@Cascading
	public ObjectProperty<DefinitionComponent> componentProperty() {
		return component;
	}

	// Templates
	private final ListProperty<DefinitionComponent> templates = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<DefinitionComponent> getTemplates() {
		return templates.get();
	}

	public void setTemplates(List<DefinitionComponent> templates) {
		this.templates.set(FXCollections.observableList(templates));
	}

	@Cascading
	public ListProperty<DefinitionComponent> templatesProperty() {
		return templates;
	}
}
