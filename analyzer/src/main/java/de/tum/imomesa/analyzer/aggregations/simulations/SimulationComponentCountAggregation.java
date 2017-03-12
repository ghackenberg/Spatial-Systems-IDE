package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.simulator.events.StartEvent;

public class SimulationComponentCountAggregation extends CountAggregtion<DefinitionComponent> {

	public SimulationComponentCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Integer> generateResult() {
		boolean integration = false;

		Map<DefinitionComponent, Integer> counts = new HashMap<>();

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					DefinitionComponent component = start.getComponent();
					if (!counts.containsKey(component)) {
						counts.put(component, 0);
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
					DefinitionComponent component = start.getComponent();
					counts.put(component, counts.get(component) + 1);
				}
			}
		}

		return counts;
	}

}
