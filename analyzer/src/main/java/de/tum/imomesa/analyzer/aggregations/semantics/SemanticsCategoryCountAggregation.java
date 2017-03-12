package de.tum.imomesa.analyzer.aggregations.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class SemanticsCategoryCountAggregation extends CountAggregtion<String> {

	public SemanticsCategoryCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		/*
		boolean integration = false;
		*/

		for (Event event : getEvents()) {
			/*
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
			*/
				if (event instanceof SimulationMarkerEvent) {
					SimulationMarkerEvent add = (SimulationMarkerEvent) event;
					SimulationMarker marker = add.getMarker();
					String name = Namer.dispatch(marker);
					if (name != null) {
						if (!counts.containsKey(name)) {
							counts.put(name, 0);
						}
						counts.put(name, counts.get(name) + 1);
					}
				}
			/*
			}
			*/
		}

		return counts;
	}

}
