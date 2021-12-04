package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.tabs.GenericTab;

public class MainComponentBottomRightTabPane extends AbstractTabPane {

	public MainComponentBottomRightTabPane(DefinitionComponent project) {
		getTabs().add(new GenericTab("Scene", ImageHelper.getIcon(Requirement.class)));
	}

}
