package de.tum.imomesa.workbench.view.trees.cells;

import de.tum.imomesa.model.NamedElement;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;

public class ObjectTreeCell extends TreeCell<Object> {
	public interface Transformer {
		public ContextMenu transform(Object object);
	}

	private Transformer transformer;

	public ObjectTreeCell(Transformer transformer) {
		this.transformer = transformer;
	}

	@Override
	public void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);
		textProperty().unbind();
		if (empty) {
			setText(null);
			setStyle(null);
			setGraphic(null);
			setContextMenu(null);
		} else {
			if (isEditing() == false) {
				if (item == null) {
					setText("Null");
					setStyle("-fx-font-style: italic;");
				} else {
					if (item instanceof NamedElement) {
						textProperty().bind(((NamedElement) item).nameProperty());
					} else {
						textProperty().bind(new ReadOnlyStringWrapper(item.toString()));
					}
					setStyle("-fx-font-style: normal;");
					setContextMenu(transformer.transform(item));
				}
				setGraphic(getTreeItem().getGraphic());
			} else {
				throw new IllegalStateException();
			}
		}
	}
}
