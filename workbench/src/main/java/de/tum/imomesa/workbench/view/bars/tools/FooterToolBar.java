package de.tum.imomesa.workbench.view.bars.tools;

import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;

public class FooterToolBar extends ToolBar {

	public FooterToolBar() {
		getItems().add(new Label("(c) 2022 Dr. Georg Hackenberg <georg.hackenberg@fh-wels.at>, School of Engineering, University of Applied Sciences Upper Austria"));
	}

}
