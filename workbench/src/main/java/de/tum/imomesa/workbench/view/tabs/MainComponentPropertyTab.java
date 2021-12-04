package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentPropertyTab extends AbstractTab {

	public MainComponentPropertyTab(DefinitionComponent component) {
		super("Properties", ImageHelper.getIcon(Property.class));
		top.getItems().add(new Label("Tools"));
	}

}
