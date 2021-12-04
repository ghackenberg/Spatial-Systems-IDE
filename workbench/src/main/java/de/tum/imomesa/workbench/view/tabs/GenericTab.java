package de.tum.imomesa.workbench.view.tabs;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class GenericTab extends AbstractTab {

	public GenericTab(String text, Node graphic) {
		super(text, graphic);
		top.getItems().add(new Label("Tools"));
	}

}
