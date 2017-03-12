package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TransitionAggregation;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.StartEvent;

public class SimulationComponentTransitionAggregation extends TransitionAggregation<DefinitionComponent> {

	public SimulationComponentTransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Map<DefinitionComponent, Integer>> generateResult() {
		// Calculate counts
		Map<DefinitionComponent, Map<DefinitionComponent, Integer>> counts = new HashMap<>();

		boolean integration = false;
		DefinitionComponent previous = null;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					DefinitionComponent current = start.getComponent();
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
