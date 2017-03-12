package de.tum.imomesa.analyzer.aggregations.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ElementComponentCountAggregation extends CountAggregtion<DefinitionComponent> {

	public ElementComponentCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Integer> generateResult() {
		Map<DefinitionComponent, Integer> counts = new HashMap<>();

		for (Change change : getChanges()) {
			if (Mapper.mapFeature(change) != null) {
				DefinitionComponent component = Mapper.mapComponent(change);
				if (component != null) {
					if (!counts.containsKey(component)) {
						counts.put(component, 0);
					}
				}
			}
		}

		for (Change change : getChanges()) {
			if (Mapper.mapFeature(change) != null) {
				DefinitionComponent component = Mapper.mapComponent(change);
				if (component != null) {
					if (change instanceof ManageChange) {
						counts.put(component, counts.get(component) + 1);
					}
					if (change instanceof ReleaseChange) {
						counts.put(component, counts.get(component) - 1);
					}
				}
			}
		}

		return counts;
	}

}
