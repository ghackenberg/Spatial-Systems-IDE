package de.tum.imomesa.workbench.explorers.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.explorers.Configuration;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;

/**
 * The TreeItem has a NamedElement as value When the name of the NamedElement
 * changes, the TreeItem value is not updated automatically Therefore, the
 * NamedElement is extracted, the value of the TreeItem set to NULL and the
 * previously extracted value set again as value
 * 
 * @author Thomas
 */
public class ElementChangeListener<T extends Element> implements ChangeListener<T> {

	private Class<T> type;
	private TreeItem<Element> item;
	private Configuration configuration;

	public ElementChangeListener(Class<T> type, TreeItem<Element> item, Configuration configuration) {
		this.type = type;
		this.item = item;
		this.configuration = configuration;
	}

	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		item.setValue(newValue);

		item.getChildren().clear();

		TreeItemBuilder.getInstance().dispatch(newValue, item, configuration);

		if (newValue != null) {
			item.setGraphic(ImageHelper.getIcon(newValue.getClass()));
		} else {
			item.setGraphic(ImageHelper.getIcon(type));
		}
	}
}
