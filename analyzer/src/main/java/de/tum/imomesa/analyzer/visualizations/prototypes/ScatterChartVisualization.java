package de.tum.imomesa.analyzer.visualizations.prototypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

public class ScatterChartVisualization<T>
		extends Visualization<Map<T, Map<Long, String>>, ScatterChart<Number, String>> {

	private List<T> order;

	public ScatterChartVisualization(Map<T, Map<Long, String>> data, List<T> order) {
		super(data);

		this.order = order;
	}

	@Override
	protected ScatterChart<Number, String> generateResult() {
		ScatterChart<Number, String> chart = new ScatterChart<>(new NumberAxis(), new CategoryAxis());

		Map<String, XYChart.Series<Number, String>> series = new HashMap<>();

		for (T key : order) {
			Map<Long, String> values = getData().get(key);

			for (Entry<Long, String> innerEntry : values.entrySet()) {
				if (!series.containsKey(innerEntry.getValue())) {
					XYChart.Series<Number, String> tempSeries = new XYChart.Series<>();

					tempSeries.setName(innerEntry.getValue());

					series.put(innerEntry.getValue(), tempSeries);

					chart.getData().add(tempSeries);
				}
			}
		}

		for (T key : order) {
			Map<Long, String> values = getData().get(key);
			// System.out.println(outerEntry.getKey().toString());

			String name = Namer.dispatch(key);

			for (Entry<Long, String> innerEntry : values.entrySet()) {
				// System.out.println(innerEntry.getKey() / 1000.0 / 60.0 /
				// 60.0);

				double value = innerEntry.getKey() / 1000.0 / 60.0 / 60.0;

				XYChart.Data<Number, String> data = new XYChart.Data<>(value, name);

				series.get(innerEntry.getValue()).getData().add(data);
			}
		}

		return chart;
	}

}
