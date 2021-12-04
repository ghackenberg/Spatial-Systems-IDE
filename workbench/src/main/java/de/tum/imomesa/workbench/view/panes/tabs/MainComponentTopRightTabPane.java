package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.tabs.GenericTab;

public class MainComponentTopRightTabPane extends AbstractTabPane {

	public MainComponentTopRightTabPane(DefinitionComponent project) {
		getTabs().add(new GenericTab("Scene", ImageHelper.getImageAsIcon("icons/tabs/scene.png")));
	}

}
