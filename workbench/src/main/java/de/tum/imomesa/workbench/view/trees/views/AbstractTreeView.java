package de.tum.imomesa.workbench.view.trees.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.trees.cells.ObjectTreeCell;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public abstract class AbstractTreeView extends TreeView<Object> {

	public AbstractTreeView() {
		setCellFactory(tree -> new ObjectTreeCell(this::dispatch));
		getSelectionModel().selectedItemProperty().addListener((self, oldValue, newValue) -> {
			if (newValue != null && newValue.getValue() instanceof Element) {
				EventBus.getInstance().publish(new SelectionExplorerEvent((Element) newValue.getValue()));
			} else {
				EventBus.getInstance().publish(new SelectionExplorerEvent(null));
			}
		});
	}

	protected interface Transformer<T> {
		public TreeItem<Object> transform(T element);
	}

	protected Map<Object, TreeItem<Object>> items = new HashMap<>();

	protected <T> TreeItem<Object> create(T element) {
		TreeItem<Object> item = new TreeItem<>(element, ImageHelper.getIcon(element.getClass()));
		item.setExpanded(true);
		items.put(element, item);
		return item;
	}

	protected <T> void add(TreeItem<Object> parent, ObservableList<T> children, Transformer<T> transformer) {
		for (T child : children) {
			parent.getChildren().add(transformer.transform(child));
		}
		children.addListener((Change<? extends T> change) -> {
			while (change.next()) {
				for (T child : change.getRemoved()) {
					parent.getChildren().remove(items.remove(child));
				}
				for (T child : change.getAddedSubList()) {
					parent.getChildren().add(transformer.transform(child));
				}
			}
		});
	}

	protected MenuItem createAdd(String text, Class<?> type) {
		return new MenuItem(text, ImageHelper.getIcon(type));
	}

	protected MenuItem createDelete(String text, Class<?> type) {
		return new MenuItem(text, ImageHelper.getIconDeleted(type));
	}

	private ContextMenu dispatch(Object object) {
		Class<?> iterator = object.getClass();
		while (iterator != null) {
			try {
				Method method = getClass().getMethod("createContextMenu", iterator);
				Object result = method.invoke(this, object);
				return (ContextMenu) result;
			} catch (NoSuchMethodException e) {
				// ignore
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			iterator = iterator.getSuperclass();
		}
		return null;
	}

}
