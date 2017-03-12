package de.tum.imomesa.analyzer.aggregations.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.DurationAggregation;
import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.events.MarkerRemoveEvent;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.NaryExpression;
import de.tum.imomesa.model.expressions.UnaryExpression;

public class SyntaxClassDurationAggregation extends DurationAggregation<String> {

	public SyntaxClassDurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected DurationDescriptor<String> generateResult() {
		Map<String, List<Long>> durations = new HashMap<>();

		Map<Element, Map<Class<?>, Long>> adds = new HashMap<>();

		for (Event event : getEvents()) {

			long currentTimestamp = event.getTimestamp();

			if (event instanceof MarkerAddEvent) {
				MarkerAddEvent add = (MarkerAddEvent) event;
				SyntacticMarker marker = add.getMarker();
				Element element = marker.getElement();

				if (!adds.containsKey(element)) {
					adds.put(element, new HashMap<>());
				}
				if (!adds.get(element).containsKey(marker.getClass())) {
					adds.get(element).put(marker.getClass(), currentTimestamp);
				}
			} else if (event instanceof MarkerRemoveEvent) {
				MarkerRemoveEvent remove = (MarkerRemoveEvent) event;
				SyntacticMarker marker = remove.getMarker();
				Element element = marker.getElement();

				if (!adds.containsKey(element)) {
					System.out.println("Element does not exist: " + element.getClass().getName());
				} else if (!adds.get(element).containsKey(marker.getClass())) {
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

					if (!durations.containsKey(name)) {
						durations.put(name, new ArrayList<>());
					}

					long previousTimestamp = adds.get(element).get(marker.getClass());
					long duration = currentTimestamp - previousTimestamp;

					if (duration / 1000 / 60 > 600) {
						System.out.println("[TYPE] Duration too long: " + duration + ", " + marker.getClass().getName()
								+ ", " + element.getClass().getName());
					}

					durations.get(name).add(duration);
					adds.get(element).remove(marker.getClass());
				}
			}
		}

		return new DurationDescriptor<>(durations);
	}

}
