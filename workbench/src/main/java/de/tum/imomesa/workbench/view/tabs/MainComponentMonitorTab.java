package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainComponentMonitorTab extends AbstractTab {

	public MainComponentMonitorTab(DefinitionComponent component) {
		super("Monitors", ImageHelper.getIcon(Monitor.class));
		top.getItems().add(new Label("Tools"));
	}

}
