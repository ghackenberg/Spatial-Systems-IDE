package de.tum.imomesa.analyzer.nodes.elements;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class GroupElement extends TreeElement {

	public GroupElement(String name) {
		super(name);
	}

	private ListProperty<TreeElement> children = new SimpleListProperty<>(FXCollections.observableArrayList());

	public ObservableList<TreeElement> getChildren() {
		return children.get();
	}

	public void setChildren(List<TreeElement> children) {
		this.children.set(FXCollections.observableArrayList(children));
	}

	public ListProperty<TreeElement> childrenProperty() {
		return children;
	}

	@Override
	public Node toNode() {
		GridPane gridPane = new GridPane();

		int cols = (int) Math.ceil(Math.sqrt(getChildren().size()));
		int rows = (int) Math.ceil(getChildren().size() * 1.0 / cols);

		for (int col = 0; col < cols; col++) {
			gridPane.getColumnConstraints().add(new ColumnConstraints());
			gridPane.getColumnConstraints().get(col).setPercentWidth(100.0 / cols);
			gridPane.getColumnConstraints().get(col).setFillWidth(true);
			gridPane.getColumnConstraints().get(col).setHalignment(HPos.CENTER);
		}

		for (int row = 0; row < rows; row++) {
			gridPane.getRowConstraints().add(new RowConstraints());
			gridPane.getRowConstraints().get(row).setPercentHeight(100.0 / rows);
			gridPane.getRowConstraints().get(row).setFillHeight(true);
			gridPane.getRowConstraints().get(row).setValignment(VPos.CENTER);
		}

		for (int index = 0; index < getChildren().size(); index++) {
			Node node = getChildren().get(index).toNode();
			
			ScrollPane scrollPane = node instanceof ScrollPane ? (ScrollPane) node : new ScrollPane(node);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			
			gridPane.add(scrollPane, index % cols, index / cols);
		}

		BorderPane borderPane = new BorderPane();

		borderPane.setTop(new ToolBar(new Label(toString())));
		borderPane.setCenter(gridPane);

		return borderPane;
	}

}
