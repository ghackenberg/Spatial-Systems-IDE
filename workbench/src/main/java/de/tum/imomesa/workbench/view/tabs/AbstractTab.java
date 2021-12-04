package de.tum.imomesa.workbench.view.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public abstract class AbstractTab extends Tab {

	protected ToolBar top = new ToolBar();
	protected BorderPane content = new BorderPane();

	public AbstractTab(String text, Node graphic) {
		super(text);

		content.setTop(top);

		setGraphic(graphic);
		setContent(content);
		setClosable(false);
	}

}
