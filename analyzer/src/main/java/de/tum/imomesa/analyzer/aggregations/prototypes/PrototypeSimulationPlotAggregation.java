package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.prototypes.ScatterChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;

public class PrototypeSimulationPlotAggregation extends Aggregation<Map<Scenario, Map<Long, String>>> {

	private static final String FAILURE = "Failure";
	private static final String TIMEOUT = "Timeout";
	private static final String SUCCESS = "Success";

	public PrototypeSimulationPlotAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<Scenario, Map<Long, String>> generateResult() {
		Map<Scenario, Map<Long, String>> counts = new HashMap<>();

		StartEvent start = null;
		Scenario scenario = null;
		boolean failure = false;
		boolean timeout = false;

		for (Event event : getEvents()) {
			if (event instanceof StartEvent) {
				start = (StartEvent) event;
				scenario = start.getScenario();
				if (!counts.containsKey(scenario)) {
					counts.put(scenario, new HashMap<>());
				}
				failure = false;
				timeout = false;
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent casted = (SimulationMarkerEvent) event;
				SimulationMarker marker = casted.getMarker();
				if (marker instanceof TimeoutMarker) {
					timeout = true;
				} else if (marker instanceof ErrorMarker) {
					failure = true;
				}
			} else if (event instanceof EndEvent) {
				if (failure) {
					counts.get(scenario).put(start.getTimestamp(), FAILURE);
				} else if (timeout) {
					counts.get(scenario).put(start.getTimestamp(), TIMEOUT);
				} else {
					counts.get(scenario).put(start.getTimestamp(), SUCCESS);
				}
			}
		}

		return counts;
	}

	@Override
	protected List<Serialization<Map<Scenario, Map<Long, String>>>> generateSerializations() throws IOException {
		List<Serialization<Map<Scenario, Map<Long, String>>>> result = new ArrayList<>();

		return result;
	}

	@Override
	protected List<Visualization<Map<Scenario, Map<Long, String>>, ?>> generateVisualizations() {
		List<Scenario> order = Sorter.sortScenarioRuns(getResult());

		List<Visualization<Map<Scenario, Map<Long, String>>, ?>> result = new ArrayList<>();

		result.add(new ScatterChartVisualization<>(getResult(), order));

		return result;
	}

}
