package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentPartTab extends AbstractTab {

	public MainComponentPartTab(DefinitionComponent component) {
		super("Parts", ImageHelper.getIcon(Part.class));
		top.getItems().add(new Label("Tools"));
	}

}
