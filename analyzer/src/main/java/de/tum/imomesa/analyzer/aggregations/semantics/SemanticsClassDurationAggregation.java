package de.tum.imomesa.analyzer.aggregations.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.aggregations.DurationAggregation;
import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.errors.ConstraintViolatedError;
import de.tum.imomesa.simulator.markers.errors.PartCollisionError;

public class SemanticsClassDurationAggregation extends DurationAggregation<String> {

	public SemanticsClassDurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected DurationDescriptor<String> generateResult() {
		Map<String, List<Long>> durations = new HashMap<>();

		// boolean integration = false;

		DefinitionComponent component = null;
		Scenario scenario = null;

		Set<String> currentMarkers = new HashSet<>();
		Map<Scenario, Set<String>> previousMarkers = new HashMap<>();

		long currentTimestamp = 0;
		Map<Scenario, Long> previousTimestamps = new HashMap<>();

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
				scenario = start.getScenario();
				currentTimestamp = start.getTimestamp();
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent add = (SimulationMarkerEvent) event;
				SimulationMarker marker = add.getMarker();

				String name = Namer.dispatch(marker);

				if (marker instanceof ConstraintViolatedError) {
					name += " (" + Namer.map(marker.getContext()) + ")";
				} else if (marker instanceof PartCollisionError) {
					name += " (" + Namer.map(marker.getContext()) + " / "
							+ Namer.map(((PartCollisionError) marker).getCollisionObject()) + ")";
				}

				if (name != null) {
					currentMarkers.add(name);
				}
			} else if (event instanceof EndEvent) {
				if (previousMarkers.containsKey(scenario)) {
					for (String previousMarker : previousMarkers.get(scenario)) {
						if (!currentMarkers.contains(previousMarker)) {
							String name = Namer.dispatch(component)
									+ " - " /*
											 * + Namer.dispatch(scenario) +
											 * " - "
											 */
									+ previousMarker;

							if (!durations.containsKey(name)) {
								durations.put(name, new ArrayList<>());
							}
							durations.get(name).add(currentTimestamp - previousTimestamps.get(scenario));
						}
					}
				}

				previousTimestamps.put(scenario, currentTimestamp);
				currentTimestamp = 0;

				previousMarkers.put(scenario, currentMarkers);
				currentMarkers = new HashSet<>();

				component = null;
				scenario = null;
			}
			/*
			 * }
			 */
		}

		return new DurationDescriptor<>(durations);
	}

}
