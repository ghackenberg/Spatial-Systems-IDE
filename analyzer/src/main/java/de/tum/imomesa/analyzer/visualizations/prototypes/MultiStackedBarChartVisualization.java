package de.tum.imomesa.analyzer.visualizations.prototypes;

import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.timelines.StackedBarChartVisualization;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MultiStackedBarChartVisualization<T1, T2>
		extends Visualization<Map<T1, Map<T2, List<Integer>>>, GridPane> {

	private List<T1> outerOrder;
	private List<T2> innerOrder;
	private Discretizer discretizer;

	public MultiStackedBarChartVisualization(Map<T1, Map<T2, List<Integer>>> data, List<T1> outerOrder,
			List<T2> innerOrder, Discretizer discretizer) {
		super(data);

		this.outerOrder = outerOrder;
		this.innerOrder = innerOrder;
		this.discretizer = discretizer;
	}

	@Override
	protected GridPane generateResult() {
		ColumnConstraints constraint = new ColumnConstraints();

		constraint.setFillWidth(true);
		constraint.setPercentWidth(100);

		GridPane result = new GridPane();

		result.getColumnConstraints().add(constraint);

		int row = 0;

		for (T1 key : outerOrder) {
			Map<T2, List<Integer>> value = getData().get(key);

			StackedBarChartVisualization<T2> nested = new StackedBarChartVisualization<>(value, innerOrder,
					discretizer);

			StackedBarChart<String, Number> chart = nested.getResult();

			chart.setTitle(Namer.dispatch(key));
			chart.setMinHeight(300);

			result.add(chart, 0, row++);
		}

		return result;
	}

}
