package de.tum.imomesa.workbench.view.panes.grids;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public abstract class AbstractGridPane extends GridPane {

	public AbstractGridPane(double[] columns, double[] rows) {
		for (double column : columns) {
			ColumnConstraints constraints = new ColumnConstraints();
			constraints.setPercentWidth(column);
			getColumnConstraints().add(constraints);
		}
		for (double row : rows) {
			RowConstraints constraints = new RowConstraints();
			constraints.setPercentHeight(row);
			getRowConstraints().add(constraints);
		}
	}

}
