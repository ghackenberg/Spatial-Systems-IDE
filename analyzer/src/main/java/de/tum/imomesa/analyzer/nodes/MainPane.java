package de.tum.imomesa.analyzer.nodes;

import java.io.IOException;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.nodes.elements.AggregationElement;
import de.tum.imomesa.analyzer.nodes.elements.GroupElement;
import de.tum.imomesa.analyzer.nodes.elements.TreeElement;
import de.tum.imomesa.analyzer.nodes.elements.VisualizationElement;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class MainPane extends GridPane {

	private GroupElement rootElement;

	private TreeItem<TreeElement> rootItem;

	private TreeView<TreeElement> tree;

	private ScrollPane scroll;

	public MainPane() {
		rootElement = new GroupElement("Aggregations");

		rootItem = new TreeItem<>(rootElement, new ImageView(new Image("root.png")));
		rootItem.setExpanded(true);

		tree = new TreeView<>(rootItem);
		tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TreeElement>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<TreeElement>> observable,
					TreeItem<TreeElement> oldValue, TreeItem<TreeElement> newValue) {
				scroll.setContent(newValue.getValue().toNode());
			}
		});

		scroll = new ScrollPane();
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);

		this.add(tree, 0, 0);
		this.add(scroll, 1, 0);

		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().add(new ColumnConstraints());
		this.getColumnConstraints().get(0).setPercentWidth(20);
		this.getColumnConstraints().get(1).setPercentWidth(80);
		this.getColumnConstraints().get(1).setFillWidth(true);
		this.getColumnConstraints().get(1).setHalignment(HPos.CENTER);

		this.getRowConstraints().add(new RowConstraints());
		this.getRowConstraints().get(0).setPercentHeight(100);
		this.getRowConstraints().get(0).setFillHeight(true);
		this.getRowConstraints().get(0).setValignment(VPos.CENTER);
	}

	public void addProcessors(String groupName, Aggregation<?>... aggregations) {
		GroupElement groupElement = new GroupElement(groupName);

		TreeItem<TreeElement> groupItem = new TreeItem<>(groupElement, new ImageView(new Image("group.png")));
		groupItem.setExpanded(true);

		for (Aggregation<?> aggregation : aggregations) {
			AggregationElement aggregationElement = new AggregationElement(aggregation);
			TreeItem<TreeElement> aggregationItem = new TreeItem<>(aggregationElement,
					new ImageView(new Image("aggregation.png")));

			aggregationItem.setExpanded(false);

			try {
				for (Serialization<?> serialization : aggregation.getSerializations()) {
					serialization.generateResult();
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

			for (Visualization<?, ?> visualization : aggregation.getVisualizations()) {
				VisualizationElement visualizationElement = new VisualizationElement(visualization);
				TreeItem<TreeElement> visualizationItem = new TreeItem<>(visualizationElement,
						new ImageView(new Image("visualization.png")));

				visualizationItem.setExpanded(false);

				aggregationElement.getChildren().add(visualizationElement);
				aggregationItem.getChildren().add(visualizationItem);
			}

			groupElement.getChildren().add(aggregationElement);
			groupItem.getChildren().add(aggregationItem);
		}

		rootElement.getChildren().add(groupElement);

		rootItem.getChildren().add(groupItem);
	}

}
