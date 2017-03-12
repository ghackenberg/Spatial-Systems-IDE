package de.tum.imomesa.analyzer.aggregations.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class SemanticsCategoryTimelineAggregation extends TimelineAggregation<String> {

	public SemanticsCategoryTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<String, List<Integer>> generateResult() {
		Map<String, List<Integer>> counts = new HashMap<>();

		boolean integration = false;

		for (Event event : getEvents()) {
			if (event instanceof de.tum.imomesa.integrator.events.StartEvent) {
				integration = true;
			} else if (event instanceof de.tum.imomesa.integrator.events.EndEvent) {
				integration = false;
			} else if (!integration) {
				if (event instanceof SimulationMarkerEvent) {
					SimulationMarkerEvent add = (SimulationMarkerEvent) event;
					SimulationMarker marker = add.getMarker();
					String name = Namer.dispatch(marker);
					if (name != null) {
						if (!counts.containsKey(name)) {
							counts.put(name, new ArrayList<>());
							for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
								counts.get(name).add(0);
							}
						}
						int bin = getDiscretizer().mapBin(event.getTimestamp());
						counts.get(name).set(bin, counts.get(name).get(bin) + 1);
					}
				}
			}
		}

		return counts;
	}

}
