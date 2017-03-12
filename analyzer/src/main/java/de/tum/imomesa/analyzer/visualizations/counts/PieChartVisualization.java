package de.tum.imomesa.analyzer.visualizations.counts;

import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class PieChartVisualization<T> extends Visualization<Map<T, Integer>, PieChart> {

	private List<T> order;

	public PieChartVisualization(Map<T, Integer> data, List<T> order) {
		super(data);

		this.order = order;
	}

	@Override
	protected PieChart generateResult() {
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

		for (T key : order) {
			Integer value = getData().get(key);
			data.add(new PieChart.Data(Namer.dispatch(key), value));
		}

		return new PieChart(data);
	}

}
