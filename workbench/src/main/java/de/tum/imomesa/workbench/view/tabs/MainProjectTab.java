package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainProjectTab extends AbstractTab {

	public MainProjectTab(Project project) {
		super("Components", ImageHelper.getIcon(Component.class));
		top.getItems().add(new Label("Tools"));
	}

}
