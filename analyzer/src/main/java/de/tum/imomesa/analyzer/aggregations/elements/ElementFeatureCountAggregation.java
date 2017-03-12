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

public class ElementFeatureCountAggregation extends CountAggregtion<Class<?>> {

	public ElementFeatureCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<Class<?>, Integer> generateResult() {
		// Calculate data
		Map<Class<?>, Integer> counts = new HashMap<>();

		for (Class<?> type : Mapper.FEATURES_INCLUDED) {
			counts.put(type, 0);
		}

		for (Change change : getChanges()) {
			Class<?> type = Mapper.mapFeature(change);
			if (type != null) {
				if (change instanceof ManageChange) {
					counts.put(type, counts.get(type) + 1);
				}
				if (change instanceof ReleaseChange) {
					counts.put(type, counts.get(type) - 1);
				}
			}
		}

		return counts;
	}

}
