package de.tum.imomesa.workbench.simulations.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class PieChartHelper {

	public static PieChart convert(Observation observation) {
		PieChart chart = new PieChart();

		chart.setTitle("Value distribution");
		chart.setAnimated(false);

		return chart;
	}

	public synchronized static void update(Simulator simulator, SimulationStepEvent event, PieChart chart,
			List<Element> context, Observation element) {
		try {
			Memory memory = simulator.getMemory();

			Map<Object, Integer> counts = new HashMap<>();

			for (int step = 0; step <= event.getStep(); step++) {
				if (memory.hasValue(context, step)) {
					Object value = memory.getValue(null, context, step);
					if (!counts.containsKey(value)) {
						counts.put(value, 0);
					}
					counts.put(value, counts.get(value) + 1);
				}
			}

			ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

			for (Entry<Object, Integer> entry : counts.entrySet()) {
				data.add(new PieChart.Data("" + entry.getKey(), entry.getValue()));
			}

			chart.setData(data);
		} catch (InterruptedException e) {

		}

	}

}
