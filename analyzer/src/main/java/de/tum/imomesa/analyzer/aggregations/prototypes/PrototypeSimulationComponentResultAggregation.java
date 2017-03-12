package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.prototypes.MultiStackedAreaChartVisualization;
import de.tum.imomesa.analyzer.visualizations.prototypes.MultiStackedBarChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;

public class PrototypeSimulationComponentResultAggregation
		extends Aggregation<Map<DefinitionComponent, Map<String, List<Integer>>>> {

	private static final String FAILURE = "Failure";
	private static final String TIMEOUT = "Timeout";
	private static final String SUCCESS = "Success";

	private Discretizer discretizer;

	public PrototypeSimulationComponentResultAggregation(List<Change> changes, List<Event> events,
			Discretizer discretizer) {
		super(changes, events);

		this.discretizer = discretizer;
	}

	@Override
	protected Map<DefinitionComponent, Map<String, List<Integer>>> generateResult() {
		Map<DefinitionComponent, Map<String, List<Integer>>> result = new HashMap<>();

		StartEvent start = null;

		DefinitionComponent component = null;

		boolean failure = false;
		boolean timeout = false;

		for (Event event : getEvents()) {
			if (event instanceof StartEvent) {
				start = (StartEvent) event;

				failure = false;
				timeout = false;

				component = start.getComponent();

				if (!result.containsKey(component)) {
					result.put(component, new HashMap<>());

					result.get(component).put(FAILURE, new ArrayList<>());
					result.get(component).put(TIMEOUT, new ArrayList<>());
					result.get(component).put(SUCCESS, new ArrayList<>());

					for (Entry<String, List<Integer>> entry : result.get(component).entrySet()) {
						for (int bin = 0; bin < discretizer.getBins(); bin++) {
							entry.getValue().add(0);
						}
					}
				}
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent casted = (SimulationMarkerEvent) event;

				SimulationMarker marker = casted.getMarker();

				if (marker instanceof ErrorMarker) {
					failure = true;
				} else if (marker instanceof TimeoutMarker) {
					timeout = true;
				} else if (marker instanceof WarningMarker) {
					// ignore
				} else {
					throw new IllegalStateException("Marker type not supported: " + marker.getClass().getName());
				}
			} else if (event instanceof EndEvent) {
				int bin = discretizer.mapBin(start.getTimestamp());

				if (failure) {
					result.get(component).get(FAILURE).set(bin, result.get(component).get(FAILURE).get(bin) + 1);
				} else if (timeout) {
					result.get(component).get(TIMEOUT).set(bin, result.get(component).get(TIMEOUT).get(bin) + 1);
				} else {
					result.get(component).get(SUCCESS).set(bin, result.get(component).get(SUCCESS).get(bin) + 1);
				}
			}
		}

		return result;
	}

	@Override
	protected List<Serialization<Map<DefinitionComponent, Map<String, List<Integer>>>>> generateSerializations()
			throws IOException {
		List<Serialization<Map<DefinitionComponent, Map<String, List<Integer>>>>> result = new ArrayList<>();

		return result;
	}

	@Override
	protected List<Visualization<Map<DefinitionComponent, Map<String, List<Integer>>>, ?>> generateVisualizations() {
		List<DefinitionComponent> outerOrder = Sorter.sortNestedTimeline(getResult());

		DefinitionComponent first = outerOrder.get(0);

		List<String> innerOrder = Sorter.sortTimeline(getResult().get(first));

		List<Visualization<Map<DefinitionComponent, Map<String, List<Integer>>>, ?>> result = new ArrayList<>();

		// result.add(new MultiBarChartVisualization<>(getResult(),
		// discretizer));
		// result.add(new MultiAreaChartVisualization<>(getResult(),
		// discretizer));
		result.add(new MultiStackedBarChartVisualization<>(getResult(), outerOrder, innerOrder, discretizer));
		result.add(new MultiStackedAreaChartVisualization<>(getResult(), outerOrder, innerOrder, discretizer));

		return result;
	}

}
