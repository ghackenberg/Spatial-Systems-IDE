package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;

public class SimulationCountAggregation extends CountAggregtion<String> {

	private static final String SUCCESS = "Success";
	private static final String FAILURE = "Failure";
	private static final String TIMEOUT = "Timeout";

	public SimulationCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		counts.put(SUCCESS, 0);
		counts.put(FAILURE, 0);
		counts.put(TIMEOUT, 0);

		/*
		boolean integration = false;
		*/
		boolean failure = false;
		boolean timeout = false;

		for (Event event : getEvents()) {
			/*
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
			*/
				if (event instanceof StartEvent) {
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
						counts.put(FAILURE, counts.get(FAILURE) + 1);
					} else if (timeout) {
						counts.put(TIMEOUT, counts.get(TIMEOUT) + 1);
					} else {
						counts.put(SUCCESS, counts.get(SUCCESS) + 1);
					}
				}
			/*
			}
			*/
		}

		return counts;
	}

}
