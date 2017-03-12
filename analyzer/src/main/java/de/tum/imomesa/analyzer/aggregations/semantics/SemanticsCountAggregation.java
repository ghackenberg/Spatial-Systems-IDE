package de.tum.imomesa.analyzer.aggregations.semantics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;

public class SemanticsCountAggregation extends CountAggregtion<String> {

	private static final String DEFICIENCY = "Deficiency count";
	private static final String DEFECT = "Defect count";
	private static final String TIMEOUT = "Timeout count";

	public SemanticsCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		counts.put(DEFICIENCY, 0);
		counts.put(DEFECT, 0);
		counts.put(TIMEOUT, 0);

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
						if (marker instanceof ErrorMarker) {
							counts.put(DEFECT, counts.get(DEFECT) + 1);
						} else if (marker instanceof WarningMarker) {
							counts.put(DEFICIENCY, counts.get(DEFICIENCY) + 1);
						} else if (marker instanceof TimeoutMarker) {
							counts.put(TIMEOUT, counts.get(TIMEOUT) + 1);
						} else {
							throw new IllegalStateException(
									"Semantic marker type not supported: " + marker.getClass().getName());
						}
					}
				}
			}
		}

		return counts;
	}

}
