package de.tum.imomesa.analyzer.aggregations.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class SemanticsComponentCountAggregation extends CountAggregtion<DefinitionComponent> {

	public SemanticsComponentCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Integer> generateResult() {
		Map<DefinitionComponent, Integer> counts = new HashMap<>();

		// boolean integration = false;
		
		DefinitionComponent component = null;

		for (Event event : getEvents()) {
			/*
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
			*/
				if (event instanceof StartEvent) {
					StartEvent start = (StartEvent) event;
					component = start.getComponent();
				} else if (event instanceof SimulationMarkerEvent) {
					SimulationMarkerEvent add = (SimulationMarkerEvent) event;
					SimulationMarker marker = add.getMarker();
					String name = Namer.dispatch(marker);
					if (name != null) {
						if (!counts.containsKey(component)) {
							counts.put(component, 0);
						}
						counts.put(component, counts.get(component) + 1);
					}
				} else if (event instanceof EndEvent) {
					component = null;
				}
			/*
			}
			*/
		}

		return counts;
	}

}
