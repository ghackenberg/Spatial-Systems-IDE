package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.workbench.view.tabs.MainProjectTab;

public class MainProjectTabPane extends AbstractTabPane {

	public MainProjectTabPane(Project project) {
		getTabs().add(new MainProjectTab(project));
	}

}
