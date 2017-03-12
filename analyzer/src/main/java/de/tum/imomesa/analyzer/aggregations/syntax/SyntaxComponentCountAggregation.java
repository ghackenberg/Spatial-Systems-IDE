package de.tum.imomesa.analyzer.aggregations.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.events.MarkerRemoveEvent;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;

public class SyntaxComponentCountAggregation extends CountAggregtion<DefinitionComponent> {

	public SyntaxComponentCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Integer> generateResult() {
		Map<Element, Set<Class<?>>> markers = new HashMap<>();
		Map<DefinitionComponent, Integer> counts = new HashMap<>();

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
				DefinitionComponent component = Mapper.mapComponent(element);

				if (component != null) {
					if (!markers.containsKey(element)) {
						System.out.println("Element does not exist: " + element.getClass().getName());
					} else if (!markers.get(element).contains(marker.getClass())) {
						System.out.println("Marker does not exist: " + marker.getClass().getName());
					} else {
						if (!counts.containsKey(component)) {
							counts.put(component, 0);
						}
						counts.put(component, counts.get(component) + 1);
						markers.get(element).remove(marker.getClass());
					}
				}
			}
		}

		return counts;
	}

}
