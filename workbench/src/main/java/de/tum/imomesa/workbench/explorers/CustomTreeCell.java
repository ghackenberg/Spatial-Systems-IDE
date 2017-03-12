package de.tum.imomesa.workbench.explorers;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TreeCell;

public class CustomTreeCell extends TreeCell<Element> {
	
	@Override
    public void updateItem(Element item, boolean empty) {
        super.updateItem(item, empty);

    	textProperty().unbind();
    	
        if (empty) {
        	setText(null);
        	setStyle(null);
        	setGraphic(null);
        	setContextMenu(null);
        }
        else {
            if (isEditing() == false) {
            	if (item == null) {
                    setText("Null");
                    setStyle("-fx-font-style: italic;");
            	}
            	else {
            		if (item instanceof NamedElement) {
            			textProperty().bind(((NamedElement) item).nameProperty());
            		}
            		else {
            			textProperty().bind(new ReadOnlyStringWrapper(item.toString()));
            		}
                    setStyle("-fx-font-style: normal;");
                	setContextMenu(ContextMenuBuilder.getContextMenu(getTreeItem()));
            	}
                setGraphic(getTreeItem().getGraphic());
            }
            else {
            	throw new IllegalStateException();
            }
        }
    }
}
