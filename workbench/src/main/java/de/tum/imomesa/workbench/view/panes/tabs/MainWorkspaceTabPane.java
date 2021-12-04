package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.workbench.view.tabs.MainWorkspaceTab;

public class MainWorkspaceTabPane extends AbstractTabPane {

	public MainWorkspaceTabPane(Workspace workspace) {
		getTabs().add(new MainWorkspaceTab(workspace));
	}

}
