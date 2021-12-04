package de.tum.imomesa.workbench.view.tabs;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public abstract class AbstractTab extends Tab {

	protected ToolBar top = new ToolBar();
	protected BorderPane content = new BorderPane();

	public AbstractTab(String text, Node graphic) {
		super(text);

		content.setTop(top);
		content.setCenter(new Label("Under construction"));

		setGraphic(graphic);
		setContent(content);
		setClosable(false);
	}

}
