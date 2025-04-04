package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.StartEvent;

public class SimulationScenarioTimelineAggregation extends TimelineAggregation<Scenario> {

	public SimulationScenarioTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<Scenario, List<Integer>> generateResult() {
		Map<Scenario, List<Integer>> counts = new HashMap<>();

		boolean integration = false;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					Scenario scenario = start.getScenario();
					if (!counts.containsKey(scenario)) {
						counts.put(scenario, new ArrayList<>());
						for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
							counts.get(scenario).add(0);
						}
					}
				}
			}
		}
		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					Scenario scenario = start.getScenario();

					int bin = getDiscretizer().mapBin(event.getTimestamp());

					counts.get(scenario).set(bin, counts.get(scenario).get(bin) + 1);
				}
			}
		}

		return counts;
	}

}
