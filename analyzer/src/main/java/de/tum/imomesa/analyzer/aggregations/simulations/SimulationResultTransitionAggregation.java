package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TransitionAggregation;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;

public class SimulationResultTransitionAggregation extends TransitionAggregation<String> {

	private static final String SUCCESS = "Success";
	private static final String SUCCESS_WARNING = "Success + warning";

	private static final String FAILURE = "Failure";
	private static final String FAILURE_WARNING = "Failure + warning";

	private static final String TIMEOUT = "Timeout";
	private static final String TIMEOUT_WARNING = "Timeout + warning";

	public SimulationResultTransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Map<String, Integer>> generateResult() {
		// Calculate counts
		Map<Scenario, String> previous = new HashMap<>();
		Map<String, Map<String, Integer>> counts = new HashMap<>();

		boolean integration = false;
		boolean failure = false;
		boolean warning = false;
		boolean timeout = false;

		Scenario currentScenario = null;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					currentScenario = start.getScenario();
				} else if (event instanceof SimulationMarkerEvent) {
					SimulationMarkerEvent casted = (SimulationMarkerEvent) event;
					SimulationMarker marker = casted.getMarker();
					if (marker instanceof ErrorMarker) {
						failure = true;
					} else if (marker instanceof WarningMarker) {
						warning = true;
					} else if (marker instanceof TimeoutMarker) {
						timeout = true;
					} else {
						throw new IllegalStateException("Marker type not supported: " + marker.getClass().getName());
					}
				} else if (event instanceof EndEvent) {
					String currentResult = "";

					if (failure && warning) {
						currentResult = FAILURE_WARNING;
					} else if (timeout && warning) {
						currentResult = TIMEOUT_WARNING;
					} else if (warning) {
						currentResult = SUCCESS_WARNING;
					} else if (failure) {
						currentResult = FAILURE;
					} else if (timeout) {
						currentResult = TIMEOUT;
					} else {
						currentResult = SUCCESS;
					}

					if (previous.containsKey(currentScenario)) {
						String previousResult = previous.get(currentScenario);
						if (!previousResult.equals(currentResult)) {
							if (!counts.containsKey(previousResult)) {
								counts.put(previousResult, new HashMap<>());
							}
							if (!counts.get(previousResult).containsKey(currentResult)) {
								counts.get(previousResult).put(currentResult, 0);
							}
							counts.get(previousResult).put(currentResult,
									counts.get(previousResult).get(currentResult) + 1);
						}
					}

					previous.put(currentScenario, currentResult);

					failure = false;
					warning = false;
					timeout = false;
				}
			}
		}

		return counts;
	}

}
