package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.tabs.GenericTab;

public class MainDefinitionComponentTopLeftTabPane extends AbstractTabPane {

	public MainDefinitionComponentTopLeftTabPane(DefinitionComponent project) {
		getTabs().add(new GenericTab("Ports", ImageHelper.getIcon(Requirement.class)));
	}

}
