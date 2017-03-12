package de.tum.imomesa.workbench.controllers.simulation.editors;

import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.simulation.AbstractElementController;
import de.tum.imomesa.workbench.simulations.helpers.PieChartHelper;
import de.tum.imomesa.workbench.simulations.helpers.TimeChartHelper;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class ObservationController extends AbstractElementController<Port> {

	private XYChart<?, ?> timeChart;
	private PieChart pieChart;

	@Override
	public void render() {
		timeChart = TimeChartHelper.convert(element);
		pieChart = PieChartHelper.convert(element);

		ColumnConstraints column1 = new ColumnConstraints();

		column1.setFillWidth(true);
		column1.setPercentWidth(50);

		ColumnConstraints column2 = new ColumnConstraints();

		column2.setFillWidth(true);
		column2.setPercentWidth(50);

		RowConstraints row1 = new RowConstraints();

		row1.setFillHeight(true);
		row1.setPercentHeight(50);

		RowConstraints row2 = new RowConstraints();

		row2.setFillHeight(true);
		row2.setPercentHeight(50);

		GridPane gridPane = new GridPane();

		gridPane.setHgap(1);
		gridPane.setVgap(1);

		gridPane.getColumnConstraints().add(column1);
		gridPane.getColumnConstraints().add(column2);

		gridPane.getRowConstraints().add(row1);
		gridPane.getRowConstraints().add(row2);

		gridPane.add(timeChart, 0, 0);
		gridPane.add(pieChart, 1, 0);

		ScrollPane scrollPane = new ScrollPane(gridPane);

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		borderPane.setCenter(scrollPane);
	}

	@Override
	public void handle(SimulationStepEvent event) {
		TimeChartHelper.update(simulator, event, timeChart, extendedContext, element);
		PieChartHelper.update(simulator, event, pieChart, extendedContext, element);
	}

}
