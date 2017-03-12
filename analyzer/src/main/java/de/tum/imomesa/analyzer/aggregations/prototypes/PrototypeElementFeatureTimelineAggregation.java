package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;

public class PrototypeElementFeatureTimelineAggregation extends TimelineAggregation<String> {

	public PrototypeElementFeatureTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<String, List<Integer>> generateResult() {
		Map<String, List<Integer>> counts = new HashMap<>();

		for (Class<?> type : Mapper.FEATURES_INCLUDED) {
			String name = Namer.dispatch(type);
			
			counts.put(name + " - added", new ArrayList<>());
			counts.put(name + " - removed", new ArrayList<>());
			
			for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
				counts.get(name + " - added").add(0);
				counts.get(name + " - removed").add(0);
			}
		}

		for (Change change : getChanges()) {
			Class<?> type = Mapper.mapFeature(change);
			
			if (type != null) {
				String name = Namer.dispatch(type);
				
				int bin = getDiscretizer().mapBin(change.getTimestamp());

				if (change instanceof ManageChange) {
					counts.get(name + " - added").set(bin, counts.get(name + " - added").get(bin) + 1);
				}
				if (change instanceof ReleaseChange) {
					counts.get(name + " - removed").set(bin, counts.get(name + " - removed").get(bin) - 1);
				}
			}
		}

		return counts;
	}

}
