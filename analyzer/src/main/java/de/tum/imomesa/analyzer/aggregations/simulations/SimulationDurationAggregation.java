package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.DurationAggregation;
import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.workbench.commons.events.SimulationEndEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStartEvent;

public class SimulationDurationAggregation extends DurationAggregation<String> {

	public SimulationDurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected DurationDescriptor<String> generateResult() {
		Map<String, List<Long>> durations = new HashMap<>();
		
		durations.put("Simulations", new ArrayList<>());
		
		/*
		boolean integration = false;
		*/
		long timestamp = 0;
		
		for (Event event : getEvents()) {
			/*
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
			*/
				if (event instanceof SimulationStartEvent) {
					timestamp = event.getTimestamp();
				} else if (event instanceof SimulationEndEvent) {
					durations.get("Simulations").add(event.getTimestamp() - timestamp);
				}
			/*
			}
			*/
		}

		return new DurationDescriptor<>(durations);
	}

}
