package de.tum.imomesa.workbench.view.trees.views;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.explorers.handlers.ListAddElementHandler;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class MainExplorerTreeView extends AbstractTreeView {

	public MainExplorerTreeView() {
		setRoot(createTreeItem(StorageManager.getInstance().getWorkspace()));
	}

	// Tree items

	protected TreeItem<Object> createTreeItem(Workspace workspace) {
		TreeItem<Object> item = create(workspace);
		add(item, workspace.projectsProperty(), this::createTreeItem);
		return item;
	}

	protected TreeItem<Object> createTreeItem(Project project) {
		TreeItem<Object> item = create(project);
		add(item, project.templatesProperty(), this::createTreeItem);
		return item;
	}

	protected TreeItem<Object> createTreeItem(Component<?> component) {
		if (component instanceof DefinitionComponent) {
			return createTreeItem((DefinitionComponent) component);
		} else if (component instanceof ReferenceComponent) {
			return createTreeItem((ReferenceComponent) component);
		} else {
			throw new IllegalStateException();
		}
	}

	protected TreeItem<Object> createTreeItem(DefinitionComponent definition) {
		TreeItem<Object> item = create(definition);
		add(item, definition.componentsProperty(), this::createTreeItem);
		return item;
	}

	protected TreeItem<Object> createTreeItem(ReferenceComponent reference) {
		return create(reference);
	}

	// Context menus

	public ContextMenu createContextMenu(Workspace workspace) {
		MenuItem add = createAdd("Add project", Project.class);
		add.setOnAction(new ListAddElementHandler<>(workspace, workspace.projectsProperty(), Project.class));
		return new ContextMenu(add);
	}

	public ContextMenu createContextMenu(Project project) {
		MenuItem add = createAdd("Add component definition", DefinitionComponent.class);
		add.setOnAction(new ListAddElementHandler<>(project, project.templatesProperty(), DefinitionComponent.class));
		MenuItem del = createDelete("Delete project", Project.class);
		del.setOnAction(new RemoveElementHandler(project));
		return new ContextMenu(add, del);
	}

	public ContextMenu createContextMenu(DefinitionComponent definition) {
		MenuItem def = createAdd("Add component definition", DefinitionComponent.class);
		def.setOnAction(new ListAddElementHandler<>(definition, definition.componentsProperty(), DefinitionComponent.class));
		MenuItem ref = createAdd("Add component reference", ReferenceComponent.class);
		ref.setOnAction(new ListAddElementHandler<>(definition, definition.componentsProperty(), ReferenceComponent.class));
		MenuItem del = createDelete("Delete component definition", DefinitionComponent.class);
		del.setOnAction(new RemoveElementHandler(definition));
		return new ContextMenu(def, ref, del);
	}

	public ContextMenu createContextMenu(ReferenceComponent reference) {
		MenuItem del = createDelete("Delete component reference", ReferenceComponent.class);
		del.setOnAction(new RemoveElementHandler(reference));
		return new ContextMenu(del);
	}

}
