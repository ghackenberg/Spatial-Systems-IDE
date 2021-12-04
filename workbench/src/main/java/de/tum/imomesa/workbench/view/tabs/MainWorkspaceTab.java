package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainWorkspaceTab extends AbstractTab {

	public MainWorkspaceTab(Workspace workspace) {
		super("Projects", ImageHelper.getIcon(Project.class));
		top.getItems().add(new Label("Tools"));
	}

}
