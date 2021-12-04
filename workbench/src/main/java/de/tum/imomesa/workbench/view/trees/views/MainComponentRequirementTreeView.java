package de.tum.imomesa.workbench.view.trees.views;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.explorers.handlers.ListAddElementHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class MainComponentRequirementTreeView extends AbstractTreeView {

	public MainComponentRequirementTreeView(DefinitionComponent component) {
		setRoot(createTreeItem(component));
	}

	// Tree items

	protected TreeItem<Object> createTreeItem(DefinitionComponent definition) {
		TreeItem<Object> item = create(definition);
		add(item, definition.requirementsProperty(), this::createTreeItem);
		return item;
	}

	protected TreeItem<Object> createTreeItem(Requirement requirement) {
		TreeItem<Object> item = create(requirement);
		add(item, requirement.requirementsProperty(), this::createTreeItem);
		return item;
	}

	// Context menus

	public ContextMenu createContextMenu(DefinitionComponent definition) {
		MenuItem add = createAdd("Add requirement", Requirement.class);
		add.setOnAction(new ListAddElementHandler<>(definition, definition.requirementsProperty(), Requirement.class));
		return new ContextMenu(add);
	}

	public ContextMenu createContextMenu(Requirement requirement) {
		MenuItem add = createAdd("Add requirement", Requirement.class);
		add.setOnAction(new ListAddElementHandler<>(requirement, requirement.requirementsProperty(), Requirement.class));
		return new ContextMenu(add);
	}

}
