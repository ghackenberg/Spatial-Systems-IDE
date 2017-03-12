package de.tum.imomesa.workbench.simulations.helpers;

import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

public class TimeChartHelper {

	public static XYChart<?, ?> convert(Observation observation) {
		XYChart<?, ?> chart = null;

		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Step");

		if (Boolean.class.isAssignableFrom(observation.getReadType())) {
			CategoryAxis yAxis = new CategoryAxis();
			yAxis.setLabel("Value");

			XYChart.Series<Number, String> series = new XYChart.Series<>();
			series.setName(observation.getName());

			ScatterChart<Number, String> scatter = new ScatterChart<>(xAxis, yAxis);
			scatter.getData().add(series);

			chart = scatter;
		} else if (Number.class.isAssignableFrom(observation.getReadType())) {
			NumberAxis yAxis = new NumberAxis();
			yAxis.setLabel("Value");

			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(observation.getName());

			LineChart<Number, Number> line = new LineChart<>(xAxis, yAxis);
			line.getData().add(series);

			chart = line;
		} else if (RealMatrix.class.isAssignableFrom(observation.getReadType())) {
			NumberAxis yAxis = new NumberAxis();
			yAxis.setLabel("Value");

			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(observation.getName());

			LineChart<Number, Number> line = new LineChart<>(xAxis, yAxis);
			line.getData().add(series);

			chart = line;
		} else if (Set.class.isAssignableFrom(observation.getReadType())) {
			NumberAxis yAxis = new NumberAxis();
			yAxis.setLabel("Value");

			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(observation.getName());

			LineChart<Number, Number> line = new LineChart<>(xAxis, yAxis);
			line.getData().add(series);

			chart = line;
		} else {
			throw new IllegalStateException("Read type not supported: " + observation.getReadType().getName());
		}
		// chart.setLegendVisible(false);
		chart.setTitle("Time series");
		chart.setAnimated(false);

		return chart;
	}

	@SuppressWarnings("unchecked")
	public synchronized static void update(Simulator simulator, SimulationStepEvent event, XYChart<?, ?> chart,
			List<Element> context, Observation element) {
		try {
			Memory memory = simulator.getMemory();

			if (Boolean.class.isAssignableFrom(element.getReadType())) {
				ScatterChart<Number, String> scatter = (ScatterChart<Number, String>) chart;
				XYChart.Series<Number, String> series = scatter.getData().get(0);
				for (int step = series.getData().size(); step <= event.getStep(); step++) {
					if (memory.hasValue(context, step)) {
						Object value = memory.getValue(null, context, step);
						series.getData().add(step, new XYChart.Data<>(step, "" + value));
					}
				}
				while (series.getData().size() > event.getStep() + 1) {
					series.getData().remove(series.getData().size() - 1);
				}
			} else if (Number.class.isAssignableFrom(element.getReadType())) {
				LineChart<Number, Number> line = (LineChart<Number, Number>) chart;
				XYChart.Series<Number, Number> series = line.getData().get(0);
				for (int step = series.getData().size(); step <= event.getStep(); step++) {
					if (memory.hasValue(context, step)) {
						Number value = (Number) memory.getValue(null, context, step);
						if (value != null) {
							series.getData().add(step, new XYChart.Data<>(step, value));
						} else {
							series.getData().add(step, new XYChart.Data<>(step, 0));
						}
					}
				}
				while (series.getData().size() > event.getStep() + 1) {
					series.getData().remove(series.getData().size() - 1);
				}
			} else if (RealMatrix.class.isAssignableFrom(element.getReadType())) {
				LineChart<Number, Number> line = (LineChart<Number, Number>) chart;
				XYChart.Series<Number, Number> series = line.getData().get(0);
				for (int step = series.getData().size(); step <= event.getStep(); step++) {
					if (memory.hasValue(context, step)) {
						RealMatrix value = (RealMatrix) memory.getValue(null, context, step);
						if (value != null) {
							EigenDecomposition decomp = new EigenDecomposition(value);
							series.getData().add(new XYChart.Data<>(step, decomp.getEigenvector(0).getNorm()));
						} else {
							series.getData().add(new XYChart.Data<>(step, 0));
						}
					}
				}
				while (series.getData().size() > event.getStep() + 1) {
					series.getData().remove(series.getData().size() - 1);
				}
			} else if (Set.class.isAssignableFrom(element.getReadType())) {
				LineChart<Number, Number> line = (LineChart<Number, Number>) chart;
				XYChart.Series<Number, Number> series = line.getData().get(0);
				for (int step = series.getData().size(); step <= event.getStep(); step++) {
					if (memory.hasValue(context, step)) {
						Set<?> value = (Set<?>) memory.getValue(null, context, step);
						if (value != null) {
							series.getData().add(new XYChart.Data<>(step, value.size()));
						} else {
							series.getData().add(new XYChart.Data<>(step, 0));
						}
					}
				}
				while (series.getData().size() > event.getStep() + 1) {
					series.getData().remove(series.getData().size() - 1);
				}
			} else {
				throw new IllegalStateException("Read type not supported: " + element.getReadType().getName());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
