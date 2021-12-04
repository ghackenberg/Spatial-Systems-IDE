package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.trees.views.MainExplorerTreeView;
import javafx.scene.control.Label;

public class MainExplorerTab extends AbstractTab {

	public MainExplorerTab() {
		super("Explorer", ImageHelper.getIcon(Component.class));
		top.getItems().add(new Label("Tools"));
		content.setCenter(new MainExplorerTreeView());
	}

}
