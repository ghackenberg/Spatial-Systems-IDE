package de.tum.imomesa.workbench.view.tabs;

import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.scene.control.Label;

public class MainAttributeTab extends AbstractTab {

	public MainAttributeTab() {
		super("Attributes", ImageHelper.getImageAsIcon("icons/tabs/attribute.png"));
		top.getItems().add(new Label("Tools"));
	}

}
