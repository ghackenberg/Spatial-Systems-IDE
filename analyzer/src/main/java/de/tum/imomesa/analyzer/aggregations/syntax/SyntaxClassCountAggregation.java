package de.tum.imomesa.analyzer.aggregations.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
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

public class SyntaxClassCountAggregation extends CountAggregtion<String> {

	public SyntaxClassCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<Element, Set<Class<?>>> markers = new HashMap<>();
		Map<String, Integer> counts = new HashMap<>();

		for (Event event : getEvents()) {
			if (event instanceof MarkerAddEvent) {
				MarkerAddEvent add = (MarkerAddEvent) event;
				SyntacticMarker marker = add.getMarker();
				Element element = marker.getElement();

				if (!markers.containsKey(element)) {
					markers.put(element, new HashSet<>());
				}
				if (!markers.get(element).contains(marker.getClass())) {
					markers.get(element).add(marker.getClass());
				}
			} else if (event instanceof MarkerRemoveEvent) {
				MarkerRemoveEvent remove = (MarkerRemoveEvent) event;
				SyntacticMarker marker = remove.getMarker();
				Element element = marker.getElement();

				if (!markers.containsKey(element)) {
					System.out.println("Element does not exist: " + element.getClass().getName());
				} else if (!markers.get(element).contains(marker.getClass())) {
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
						counts.put(name, 0);
					}

					counts.put(name, counts.get(name) + 1);
					markers.get(element).remove(marker.getClass());
				}
			}
		}

		return counts;
	}

}
