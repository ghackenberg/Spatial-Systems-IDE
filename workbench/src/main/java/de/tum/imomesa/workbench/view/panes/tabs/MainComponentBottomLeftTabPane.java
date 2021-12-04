package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.view.tabs.GenericTab;

public class MainComponentBottomLeftTabPane extends AbstractTabPane {

	public MainComponentBottomLeftTabPane(DefinitionComponent component) {
		getTabs().add(new GenericTab("Settings", ImageHelper.getImageAsIcon("icons/tabs/attribute.png")));
	}

}
