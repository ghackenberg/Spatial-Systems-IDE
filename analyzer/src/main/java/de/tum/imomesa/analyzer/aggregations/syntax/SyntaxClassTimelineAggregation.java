package de.tum.imomesa.analyzer.aggregations.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.events.MarkerRemoveEvent;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;

public class SyntaxClassTimelineAggregation extends TimelineAggregation<String> {

	public SyntaxClassTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<String, List<Integer>> generateResult() {
		Map<Element, Map<Class<?>, Long>> markers = new HashMap<>();
		Map<String, List<Integer>> counts = new HashMap<>();

		for (Event event : getEvents()) {
			if (event instanceof MarkerAddEvent) {
				MarkerAddEvent add = (MarkerAddEvent) event;
				SyntacticMarker marker = add.getMarker();
				Element element = marker.getElement();

				if (!markers.containsKey(element)) {
					markers.put(element, new HashMap<>());
				}
				if (!markers.get(element).containsKey(marker.getClass())) {
					markers.get(element).put(marker.getClass(), add.getTimestamp());
				}
			} else if (event instanceof MarkerRemoveEvent) {
				MarkerRemoveEvent remove = (MarkerRemoveEvent) event;
				SyntacticMarker marker = remove.getMarker();
				Element element = marker.getElement();
				DefinitionComponent component = Mapper.mapComponent(element);

				if (component != null) {
					if (!markers.containsKey(element)) {
						System.out.println("Element does not exist: " + element.getClass().getName());
					} else if (!markers.get(element).containsKey(marker.getClass())) {
						System.out.println("Marker does not exist: " + marker.getClass().getName());
					} else {
						String name = Namer.dispatch(marker) + " - " + marker.getClass().getSimpleName() + " - ";

						if (marker.getElement() instanceof Observation) {
							name += "Observation";
						} else if (marker.getElement() instanceof Executable<?, ?>) {
							name += "Executable";
						} else if (marker.getElement() instanceof NaryExpression) {
							name += "NaryExpression";
						} else if (marker.getElement() instanceof UnaryExpression) {
							name += "UnaryExpression";
						} else if (marker.getElement() instanceof Expression) {
							name += "Expression";
						} else {
							name += marker.getElement().getClass().getSimpleName();
						}

						if (!counts.containsKey(name)) {
							counts.put(name, new ArrayList<>());
							for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
								counts.get(name).add(0);
							}
						}

						long startTime = markers.get(element).get(marker.getClass());
						long endTime = remove.getTimestamp();

						int startBin = getDiscretizer().mapBin(startTime);
						int endBin = getDiscretizer().mapBin(endTime);

						for (int bin = startBin; bin <= endBin; bin++) {
							counts.get(name).set(bin, counts.get(name).get(bin) + 1);
						}

						markers.get(element).remove(marker.getClass());
					}
				}
			}
		}

		return counts;
	}

}
