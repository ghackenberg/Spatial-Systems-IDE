package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.StartEvent;

public class SimulationScenarioCountAggregation extends CountAggregtion<Scenario> {

	public SimulationScenarioCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<Scenario, Integer> generateResult() {
		Map<Scenario, Integer> counts = new HashMap<>();
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
						counts.put(scenario, 0);
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
					counts.put(scenario, counts.get(scenario) + 1);
				}
			}
		}

		return counts;
	}

}
