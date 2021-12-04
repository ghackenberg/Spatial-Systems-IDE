package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.tabs.GenericTab;

public class MainReferenceComponentTabPane extends AbstractTabPane {

	public MainReferenceComponentTabPane(ReferenceComponent reference) {
		getTabs().add(new GenericTab("Settings", ImageHelper.getIcon(Requirement.class)));
	}

}
