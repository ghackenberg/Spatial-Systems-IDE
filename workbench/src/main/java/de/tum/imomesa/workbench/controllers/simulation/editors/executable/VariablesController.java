package de.tum.imomesa.workbench.controllers.simulation.editors.executable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.simulation.AbstractElementController;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import de.tum.imomesa.workbench.simulations.helpers.TimeChartHelper;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class VariablesController extends AbstractElementController<OverviewElement<Variable>> {

	private Executable<?, ?> parent;
	private Map<Variable, XYChart<?, ?>> charts = new HashMap<>();

	@Override
	public void render() {
		parent = (Executable<?, ?>) extendedContext.get(extendedContext.size() - 1);

		List<Variable> ports = parent.getVariables();

		GridPane gridPane = new GridPane();

		gridPane.setHgap(1);
		gridPane.setVgap(1);

		int cols = (int) Math.ceil(Math.sqrt(ports.size()));
		int rows = cols; // (int) Math.ceil(ports.size() * 1.0 / cols);

		for (int col = 0; col < cols; col++) {
			ColumnConstraints constraint = new ColumnConstraints();
			constraint.setFillWidth(true);
			constraint.setPercentWidth(100.0 / cols);

			gridPane.getColumnConstraints().add(constraint);
		}
		for (int row = 0; row < rows; row++) {
			RowConstraints constraint = new RowConstraints();
			constraint.setFillHeight(true);
			constraint.setPercentHeight(100.0 / rows);

			gridPane.getRowConstraints().add(constraint);
		}

		for (int index = 0; index < ports.size(); index++) {
			int col = index % cols;
			int row = index / cols;

			Variable port = ports.get(index);

			XYChart<?, ?> chart = TimeChartHelper.convert(port);

			charts.put(port, chart);

			gridPane.add(chart, col, row);
		}

		ScrollPane scrollPane = new ScrollPane(gridPane);

		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		borderPane.setCenter(scrollPane);
	}

	@Override
	public void handle(SimulationStepEvent event) {
		for (Entry<Variable, XYChart<?, ?>> entry : charts.entrySet()) {
			TimeChartHelper.update(simulator, event, entry.getValue(), entry.getKey().append(extendedContext),
					entry.getKey());
		}
	}

}
