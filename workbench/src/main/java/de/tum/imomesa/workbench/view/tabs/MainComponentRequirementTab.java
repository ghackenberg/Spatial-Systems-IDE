package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentRequirementTab extends AbstractTab {

	public MainComponentRequirementTab(DefinitionComponent component) {
		super("Requirements", ImageHelper.getIcon(Requirement.class));
		top.getItems().add(new Label("Tools"));
	}

}
