package de.tum.imomesa.analyzer.aggregations.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;

public class ElementFeatureTimelineAggregation extends TimelineAggregation<Class<?>> {

	public ElementFeatureTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<Class<?>, List<Integer>> generateResult() {
		Map<Class<?>, List<Integer>> counts = new HashMap<>();

		for (Class<?> type : Mapper.FEATURES_INCLUDED) {
			counts.put(type, new ArrayList<>());
			for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
				counts.get(type).add(0);
			}
		}

		for (Change change : getChanges()) {
			Class<?> type = Mapper.mapFeature(change);
			if (type != null) {
				int bin = getDiscretizer().mapBin(change.getTimestamp());

				if (change instanceof ManageChange) {
					counts.get(type).set(bin, counts.get(type).get(bin) + 1);
				}
				if (change instanceof ReleaseChange) {
					counts.get(type).set(bin, counts.get(type).get(bin) - 1);
				}
			}
		}

		return counts;
	}

}
