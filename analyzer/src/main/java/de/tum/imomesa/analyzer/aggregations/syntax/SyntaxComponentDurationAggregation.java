package de.tum.imomesa.analyzer.aggregations.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.DurationAggregation;
import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.events.MarkerRemoveEvent;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;

public class SyntaxComponentDurationAggregation extends DurationAggregation<DefinitionComponent> {

	public SyntaxComponentDurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected DurationDescriptor<DefinitionComponent> generateResult() {
		Map<DefinitionComponent, List<Long>> durations = new HashMap<>();
		
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
				DefinitionComponent component = Mapper.mapComponent(element);

				if (component != null) {
					if (!adds.containsKey(element)) {
						System.out.println("Element does not exist: " + element.getClass().getName());
					} else if (!adds.get(element).containsKey(marker.getClass())) {
						System.out.println("Marker does not exist: " + marker.getClass().getName());
					} else {
						if (!durations.containsKey(component)) {
							durations.put(component, new ArrayList<>());
						}

						long previousTimestamp = adds.get(element).get(marker.getClass());
						long duration = currentTimestamp - previousTimestamp;

						if (duration / 1000 / 60 > 600) {
							System.out.println("[Component] Duration too long: " + duration + ", "
									+ marker.getClass().getName() + ", " + element.getClass().getName());
						}

						durations.get(component).add(duration);
						adds.get(element).remove(marker.getClass());
					}
				}
			}
		}

		return new DurationDescriptor<>(durations);
	}

}
