package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentBehaviorTab extends AbstractTab {

	public MainComponentBehaviorTab(DefinitionComponent component) {
		super("Behaviors", ImageHelper.getIcon(Behavior.class));
		top.getItems().add(new Label("Tools"));
	}

}
