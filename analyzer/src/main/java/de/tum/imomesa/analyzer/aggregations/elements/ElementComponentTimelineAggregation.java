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
import de.tum.imomesa.model.components.DefinitionComponent;

public class ElementComponentTimelineAggregation extends TimelineAggregation<DefinitionComponent> {

	public ElementComponentTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<DefinitionComponent, List<Integer>> generateResult() {
		Map<DefinitionComponent, List<Integer>> counts = new HashMap<>();

		for (Change change : getChanges()) {
			if (Mapper.mapFeature(change) != null) {
				DefinitionComponent component = Mapper.mapComponent(change);
				if (component != null) {
					if (!counts.containsKey(component)) {
						counts.put(component, new ArrayList<>());
						for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
							counts.get(component).add(0);
						}
					}
				}
			}
		}

		for (Change change : getChanges()) {
			if (Mapper.mapFeature(change) != null) {
				DefinitionComponent component = Mapper.mapComponent(change);
				if (component != null) {
					int bin = getDiscretizer().mapBin(change.getTimestamp());

					if (change instanceof ManageChange) {
						counts.get(component).set(bin, counts.get(component).get(bin) + 1);
					}
					if (change instanceof ReleaseChange) {
						counts.get(component).set(bin, counts.get(component).get(bin) - 1);
					}
				}
			}
		}

		return counts;
	}

}
