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
import de.tum.imomesa.database.changes.UpdateChange;

public class ElementCountAggregation extends CountAggregtion<String> {

	private static final String MANAGE = "Manage events";
	private static final String UPDATE = "Manage events";
	private static final String RELEASE = "Manage events";

	public ElementCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		counts.put(MANAGE, 0);
		counts.put(UPDATE, 0);
		counts.put(RELEASE, 0);

		for (Change change : getChanges()) {
			Class<?> type = Mapper.mapFeature(change);
			if (type != null) {
				if (change instanceof ManageChange) {
					counts.put(MANAGE, counts.get(MANAGE) + 1);
				} else if (change instanceof UpdateChange) {
					counts.put(UPDATE, counts.get(UPDATE) + 1);
				} else if (change instanceof ReleaseChange) {
					counts.put(RELEASE, counts.get(RELEASE) + 1);
				}
			}
		}

		return counts;
	}

}
