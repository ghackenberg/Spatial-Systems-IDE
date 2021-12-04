package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainWelcomeTab extends AbstractTab {

	public MainWelcomeTab() {
		super("Welcome", ImageHelper.getImageAsIcon("images/icon.png"));
		top.getItems().add(new Label("Tools"));
	}

}
