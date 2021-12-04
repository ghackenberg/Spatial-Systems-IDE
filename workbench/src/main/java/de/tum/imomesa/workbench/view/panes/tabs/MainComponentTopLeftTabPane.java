package de.tum.imomesa.workbench.view.panes.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.view.tabs.MainComponentBehaviorTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentComponentTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentMonitorTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentPartTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentPortTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentPropertyTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentRequirementTab;
import de.tum.imomesa.workbench.view.tabs.MainComponentScenarioTab;

public class MainComponentTopLeftTabPane extends AbstractTabPane {

	public MainComponentTopLeftTabPane(DefinitionComponent component) {
		getTabs().add(new MainComponentRequirementTab(component));
		getTabs().add(new MainComponentPortTab(component));
		getTabs().add(new MainComponentPropertyTab(component));
		getTabs().add(new MainComponentMonitorTab(component));
		getTabs().add(new MainComponentScenarioTab(component));
		getTabs().add(new MainComponentComponentTab(component));
		getTabs().add(new MainComponentBehaviorTab(component));
		getTabs().add(new MainComponentPartTab(component));
	}

}
