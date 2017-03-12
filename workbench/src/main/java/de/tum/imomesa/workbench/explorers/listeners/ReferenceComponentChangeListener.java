package de.tum.imomesa.workbench.explorers.listeners;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.workbench.explorers.Configuration;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public class ReferenceComponentChangeListener implements ChangeListener<Boolean> {

	private TreeItem<Element> item;
	private Configuration configuration;

	private TreeItem<Element> transforms;

	public ReferenceComponentChangeListener(ReferenceComponent component, TreeItem<Element> item,
			Configuration configuration) {

		this.item = item;
		this.configuration = configuration;

		transforms = TreeItemBuilder.getInstance().transform(component, "Transforms", Transform.class,
				component.transformsProperty(), configuration, true, false);

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		update();
	}

	private void update() {
		item.getChildren().clear();

		if (configuration.getTransforms()) {
			item.getChildren().add(transforms);
		}
	}

}
