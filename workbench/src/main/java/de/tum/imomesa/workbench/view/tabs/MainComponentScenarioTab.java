package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentScenarioTab extends AbstractTab {

	public MainComponentScenarioTab(DefinitionComponent component) {
		super("Scenarios", ImageHelper.getIcon(Scenario.class));
		top.getItems().add(new Label("Tools"));
	}

}
