package de.tum.imomesa.workbench.commons.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class PTreeTableColumn<S, T> extends TreeTableColumn<S, T> {

	private ChangeListener<Number> listener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			updateWidth(newValue.doubleValue());
		}
	};
	
	public PTreeTableColumn() {
		this(null, 1);
	}

	public PTreeTableColumn(String text, double precentageWidth) {
		treeTableViewProperty().addListener(new ChangeListener<TreeTableView<S>>() {
			@Override
			public void changed(ObservableValue<? extends TreeTableView<S>> observable, TreeTableView<S> oldValue,
					TreeTableView<S> newValue) {
				if (oldValue != null) {
					oldValue.widthProperty().removeListener(listener);
				}
				if (newValue != null) {
					newValue.widthProperty().addListener(listener);
					updateWidth(newValue.getWidth());
				}
			}
		});
		setText(text);
		setPercentageWidth(precentageWidth);
	}

	private void updateWidth(double tableWidth) {
		this.setPrefWidth(Math.floor(tableWidth * getPercentageWidth()));
	}

	// Percentage width

	private final DoubleProperty percentageWidth = new SimpleDoubleProperty(1);

	public final DoubleProperty percentageWidthProperty() {
		return this.percentageWidth;
	}

	public final double getPercentageWidth() {
		return this.percentageWidthProperty().get();
	}

	public final void setPercentageWidth(double value) throws IllegalArgumentException {
		if (value >= 0 && value <= 1) {
			this.percentageWidthProperty().set(value);
		} else {
			throw new IllegalArgumentException(
					String.format("The provided percentage width is not between 0.0 and 1.0. Value is: %1$s", value));
		}
	}

}
