package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TransitionAggregation;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.StartEvent;

public class SimulationScenarioTransitionAggregation extends TransitionAggregation<Scenario> {

	public SimulationScenarioTransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<Scenario, Map<Scenario, Integer>> generateResult() {
		Map<Scenario, Map<Scenario, Integer>> counts = new HashMap<>();

		boolean integration = false;

		Scenario previous = null;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					Scenario current = start.getScenario();
					if (previous != null && !previous.equals(current)) {
						if (!counts.containsKey(previous)) {
							counts.put(previous, new HashMap<>());
						}
						if (!counts.get(previous).containsKey(current)) {
							counts.get(previous).put(current, 0);
						}
						counts.get(previous).put(current, counts.get(previous).get(current) + 1);
					}
					previous = current;
				} else if (event instanceof EndEvent) {
					// ignore
				}
			}
		}

		return counts;
	}

}
