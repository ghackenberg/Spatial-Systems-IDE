package de.tum.imomesa.analyzer.visualizations.timelines;

import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public class StackedBarChartVisualization<T>
		extends Visualization<Map<T, List<Integer>>, StackedBarChart<String, Number>> {

	private List<T> order;
	private Discretizer discretizer;

	public StackedBarChartVisualization(Map<T, List<Integer>> data, List<T> order, Discretizer discretizer) {
		super(data);

		this.order = order;
		this.discretizer = discretizer;
	}

	@Override
	protected StackedBarChart<String, Number> generateResult() {
		// Convert to chart

		StackedBarChart<String, Number> chart = new StackedBarChart<>(new CategoryAxis(), new NumberAxis());

		chart.getXAxis().setLabel("Time (in h)");
		chart.getYAxis().setLabel("Object count");

		for (T key : order) {
			XYChart.Series<String, Number> series = new XYChart.Series<>();

			series.setName(Namer.dispatch(key));

			List<Integer> values = getData().get(key);

			for (int bin = 0; bin < discretizer.getBins(); bin++) {
				int value = values != null ? values.get(bin) : 0;
				series.getData().add(new XYChart.Data<>(discretizer.mapTime(bin) + "", value));
			}
			chart.getData().add(series);
		}

		return chart;

	}

}
