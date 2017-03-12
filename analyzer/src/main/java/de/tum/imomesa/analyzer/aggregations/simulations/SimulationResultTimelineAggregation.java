package de.tum.imomesa.analyzer.aggregations.simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;

public class SimulationResultTimelineAggregation extends TimelineAggregation<String> {

	private static final String SUCCESS = "Success";
	private static final String SUCCESS_WARNING = "Success + warning";

	private static final String FAILURE = "Failure";
	private static final String FAILURE_WARNING = "Failure + warning";

	private static final String TIMEOUT = "Timeout";
	private static final String TIMEOUT_WARNING = "Timeout + warning";

	public SimulationResultTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<String, List<Integer>> generateResult() {
		Map<String, List<Integer>> counts = new HashMap<>();

		counts.put(SUCCESS, new ArrayList<>());
		counts.put(SUCCESS_WARNING, new ArrayList<>());

		counts.put(FAILURE, new ArrayList<>());
		counts.put(FAILURE_WARNING, new ArrayList<>());

		counts.put(TIMEOUT, new ArrayList<>());
		counts.put(TIMEOUT_WARNING, new ArrayList<>());

		for (Entry<String, List<Integer>> entry : counts.entrySet()) {
			for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
				entry.getValue().add(0);
			}
		}

		int bin = 0;

		boolean integration = false;
		boolean failure = false;
		boolean timeout = false;
		boolean warning = false;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof StartEvent) {
					bin = getDiscretizer().mapBin(event.getTimestamp());
					failure = false;
					timeout = false;
				} else if (event instanceof SimulationMarkerEvent) {
					SimulationMarkerEvent casted = (SimulationMarkerEvent) event;
					SimulationMarker marker = casted.getMarker();
					if (marker instanceof TimeoutMarker) {
						timeout = true;
					} else if (marker instanceof ErrorMarker) {
						failure = true;
					} else if (marker instanceof WarningMarker) {
						warning = true;
					}
				} else if (event instanceof EndEvent) {
					if (failure) {
						if (warning) {
							counts.get(FAILURE_WARNING).set(bin, counts.get(FAILURE_WARNING).get(bin) + 1);
						} else {
							counts.get(FAILURE).set(bin, counts.get(FAILURE).get(bin) + 1);
						}
					} else if (timeout) {
						if (warning) {
							counts.get(TIMEOUT_WARNING).set(bin, counts.get(TIMEOUT_WARNING).get(bin) + 1);
						} else {
							counts.get(TIMEOUT).set(bin, counts.get(TIMEOUT).get(bin) + 1);
						}
					} else {
						if (warning) {
							counts.get(SUCCESS_WARNING).set(bin, counts.get(SUCCESS_WARNING).get(bin) + 1);
						} else {
							counts.get(SUCCESS).set(bin, counts.get(SUCCESS).get(bin) + 1);
						}
					}
				}
			}
		}

		return counts;
	}

}
