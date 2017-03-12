package de.tum.imomesa.analyzer.nodes.elements;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class VisualizationElement extends TreeElement {

	private Visualization<?, ?> visualization;

	public VisualizationElement(Visualization<?, ?> visualization) {
		super(Namer.convertSpace(visualization.getClass()));

		this.visualization = visualization;
	}

	@Override
	public Node toNode() {
		BorderPane borderPane = new BorderPane();

		borderPane.setTop(new ToolBar(new Label(toString())));
		borderPane.setCenter(visualization.getResult());

		return borderPane;
	}

}
