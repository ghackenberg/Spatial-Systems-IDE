package de.tum.imomesa.workbench.explorers;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.explorers.handlers.ListAddElementHandler;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuBuilder {

	public static ContextMenu getContextMenu(ReferenceComponent c) {
		MenuItem del = new MenuItem("Delete component reference");
		del.setOnAction(new RemoveElementHandler(c));
		del.setGraphic(ImageHelper.getIconDeleted(ReferenceComponent.class));

		return new ContextMenu(del);
	}

	public static ContextMenu getContextMenu(DefinitionComponent c) {
		MenuItem def = new MenuItem("Add component definition");
		def.setOnAction(new ListAddElementHandler<>(c, c.componentsProperty(), DefinitionComponent.class));
		def.setGraphic(ImageHelper.getIcon(DefinitionComponent.class));

		MenuItem ref = new MenuItem("Add component reference");
		ref.setOnAction(new ListAddElementHandler<>(c, c.componentsProperty(), ReferenceComponent.class));
		ref.setGraphic(ImageHelper.getIcon(ReferenceComponent.class));

		MenuItem del = new MenuItem("Delete component definition");
		del.setOnAction(new RemoveElementHandler(c));
		del.setGraphic(ImageHelper.getIconDeleted(DefinitionComponent.class));

		return new ContextMenu(def, ref, del);
	}

	public static ContextMenu getContextMenu(Project p) {
		MenuItem add = new MenuItem("Add component definition");
		add.setOnAction(new ListAddElementHandler<>(p, p.templatesProperty(), DefinitionComponent.class));
		add.setGraphic(ImageHelper.getIcon(DefinitionComponent.class));

		MenuItem del = new MenuItem("Delete project");
		del.setOnAction(new RemoveElementHandler(p));
		del.setGraphic(ImageHelper.getIconDeleted(Project.class));

		return new ContextMenu(add, del);
	}

	public static ContextMenu getContextMenu(Workspace w) {
		MenuItem add = new MenuItem("Add project");
		add.setOnAction(new ListAddElementHandler<>(w, w.projectsProperty(), Project.class));
		add.setGraphic(ImageHelper.getIcon(Project.class));

		return new ContextMenu(add);
	}

	public static ContextMenu getContextMenu(Element element) {
		if (element instanceof Workspace) {
			return getContextMenu((Workspace) element);
		} else if (element instanceof Project) {
			return getContextMenu((Project) element);
		} else if (element instanceof DefinitionComponent) {
			return getContextMenu((DefinitionComponent) element);
		} else if (element instanceof ReferenceComponent) {
			return getContextMenu((ReferenceComponent) element);
		} else {
			return null;
		}
	}

}