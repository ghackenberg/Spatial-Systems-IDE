package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentComponentTab extends AbstractTab {

	public MainComponentComponentTab(DefinitionComponent component) {
		super("Components", ImageHelper.getIcon(Component.class));
		top.getItems().add(new Label("Tools"));
	}

}
