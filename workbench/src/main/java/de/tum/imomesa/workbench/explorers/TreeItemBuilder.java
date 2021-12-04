package de.tum.imomesa.workbench.explorers;

import java.util.HashMap;
import java.util.Map;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TreeItemBuilder {

	private static TreeItemBuilder INSTANCE = new TreeItemBuilder();

	public static TreeItemBuilder getInstance() {
		return INSTANCE;
	}

	// Constructor

	private Map<Element, TreeItem<Element>> items = new HashMap<>();

	private TreeItemBuilder() {

	}

	// Public

	public TreeItem<Element> process(DefinitionComponent component) {
		TreeItem<Element> item = createItem(component);

		addChildren(item, component.componentsProperty(), this::process);

		return item;
	}

	public TreeItem<Element> process(ReferenceComponent component) {
		TreeItem<Element> item = createItem(component);

		return item;
	}

	public TreeItem<Element> process(Component<?> component) {
		if (component instanceof DefinitionComponent) {
			return process((DefinitionComponent) component);
		} else if (component instanceof ReferenceComponent) {
			return process((ReferenceComponent) component);
		} else {
			throw new IllegalStateException();
		}
	}

	public TreeItem<Element> process(Project project) {
		TreeItem<Element> item = createItem(project);

		addChildren(item, project.templatesProperty(), this::process);

		return item;
	}

	public TreeItem<Element> process(Workspace workspace) {
		TreeItem<Element> item = createItem(workspace);

		addChildren(item, workspace.projectsProperty(), this::process);

		return item;
	}

	// Private

	private <T extends Element> TreeItem<Element> createItem(T element) {
		TreeItem<Element> item = new TreeItem<>(element, ImageHelper.getIcon(element.getClass()));
		item.setExpanded(true);

		items.put(element, item);

		return item;
	}

	private interface Processor<T> {
		public TreeItem<Element> process(T element);
	}

	private <T> void addChildren(TreeItem<Element> parent, ObservableList<T> children, Processor<T> processor) {
		for (T element : children) {
			parent.getChildren().add(processor.process(element));
		}
		children.addListener(createListener(parent, processor));
	}

	private <T> ListChangeListener<T> createListener(TreeItem<Element> parent, Processor<T> processor) {
		return (Change<? extends T> change) -> {
			while (change.next()) {
				for (T element : change.getRemoved()) {
					parent.getChildren().remove(items.get(element));
					items.remove(element);
				}
				for (T element : change.getAddedSubList()) {
					parent.getChildren().add(processor.process(element));
				}
			}
		};
	}

}
