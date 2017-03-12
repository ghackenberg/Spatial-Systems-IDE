package de.tum.imomesa.workbench.explorers.listeners;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public class ElementSelectionListener implements ChangeListener<TreeItem<Element>> {

	@Override
	public void changed(ObservableValue<? extends TreeItem<Element>> observable, TreeItem<Element> oldValue, TreeItem<Element> newValue) {
		// return if new item is null
		if(newValue == null) return;
		// Publish event
		EventBus.getInstance().publish(new SelectionExplorerEvent(newValue.getValue()));
	}
}
