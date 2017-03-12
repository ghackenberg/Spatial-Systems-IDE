package de.tum.imomesa.analyzer.visualizations.durations;

import java.util.List;

import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public class StackedBarChartVisualization<T>
		extends Visualization<DurationDescriptor<T>, StackedBarChart<String, Number>> {

	private List<T> order;

	public StackedBarChartVisualization(DurationDescriptor<T> data, List<T> order) {
		super(data);

		this.order = order;
	}

	@Override
	protected StackedBarChart<String, Number> generateResult() {
		// Convert to chart

		XYChart.Series<String, Number> minimumSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> averageSeries = new XYChart.Series<>();
		XYChart.Series<String, Number> maximumSeries = new XYChart.Series<>();

		minimumSeries.setName("Minimum duration");
		averageSeries.setName("Average duration");
		maximumSeries.setName("Maximum duration");

		for (T key : order) {
			String name = Namer.dispatch(key);

			double min = getData().getSortedMinimums().get(key) / 1000 / 60;
			double avg = getData().getSortedAverages().get(key) / 1000 / 60;
			double max = getData().getSortedMaximums().get(key) / 1000 / 60;

			minimumSeries.getData().add(new XYChart.Data<>(name, min));
			averageSeries.getData().add(new XYChart.Data<>(name, avg));
			maximumSeries.getData().add(new XYChart.Data<>(name, max));
		}

		StackedBarChart<String, Number> chart = new StackedBarChart<>(new CategoryAxis(), new NumberAxis());

		chart.getYAxis().setLabel("Duration (in min)");

		chart.getData().add(minimumSeries);
		chart.getData().add(averageSeries);
		chart.getData().add(maximumSeries);

		return chart;
	}

}
