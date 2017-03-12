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
import de.tum.imomesa.simulator.markers.errors.ConstraintViolatedError;
import de.tum.imomesa.simulator.markers.errors.PartCollisionError;

public class SemanticsClassCountAggregation extends CountAggregtion<String> {

	public SemanticsClassCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		// boolean integration = false;

		DefinitionComponent component = null;
		// Scenario scenario = null;

		for (Event event : getEvents()) {
			/*
			 * if (event instanceof de.tum.imomesa.integrator.events.StartEvent)
			 * { integration = true; } else if (event instanceof
			 * de.tum.imomesa.integrator.events.EndEvent) { integration = false;
			 * } else if (!integration) {
			 */
			if (event instanceof StartEvent) {
				StartEvent start = (StartEvent) event;
				component = start.getComponent();
				// scenario = start.getScenario();
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent add = (SimulationMarkerEvent) event;
				SimulationMarker marker = add.getMarker();

				String name = Namer.dispatch(component)
						+ " - " /* + Namer.dispatch(scenario) + " - " */
						+ Namer.dispatch(marker);

				if (marker instanceof ConstraintViolatedError) {
					name += " (" + Namer.map(marker.getContext()) + ")";
				} else if (marker instanceof PartCollisionError) {
					name += " (" + Namer.map(marker.getContext()) + " / "
							+ Namer.map(((PartCollisionError) marker).getCollisionObject()) + ")";
				}

				if (Namer.dispatch(marker) != null) {
					if (!counts.containsKey(name)) {
						counts.put(name, 0);
					}
					counts.put(name, counts.get(name) + 1);
				}
			} else if (event instanceof EndEvent) {
				component = null;
				// scenario = null;
			}
			/*
			 * }
			 */
		}

		return counts;
	}

}
