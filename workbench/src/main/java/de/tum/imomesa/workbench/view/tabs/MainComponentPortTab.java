package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentPortTab extends AbstractTab {

	public MainComponentPortTab(DefinitionComponent component) {
		super("Ports", ImageHelper.getIcon(Port.class));
		top.getItems().add(new Label("Tools"));
	}

}
