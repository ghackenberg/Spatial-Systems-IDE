package de.tum.imomesa.analyzer.aggregations.sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TimelineAggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.workbench.commons.events.ProgramEndEvent;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;

public class SessionTimelineAggregation extends TimelineAggregation<String> {

	private static final String SESSION = "Session";

	public SessionTimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events, discretizer);
	}

	@Override
	protected Map<String, List<Integer>> generateResult() {
		Map<String, List<Integer>> counts = new HashMap<>();

		counts.put(SESSION, new ArrayList<>());

		for (int bin = 0; bin < getDiscretizer().getBins(); bin++) {
			counts.get(SESSION).add(0);
		}

		List<ProgramStartEvent> starts = new ArrayList<>();
		List<ProgramEndEvent> ends = new ArrayList<>();

		for (Event event : getEvents()) {
			if (event instanceof ProgramStartEvent) {
				starts.add((ProgramStartEvent) event);
			}
			if (event instanceof ProgramEndEvent) {
				ends.add((ProgramEndEvent) event);
			}
		}

		if (starts.size() != ends.size()) {
			throw new IllegalStateException("Sizes must be equal!");
		}

		for (int index = 0; index < starts.size(); index++) {
			ProgramStartEvent start = starts.get(index);
			ProgramEndEvent end = ends.get(index);

			long startTimestamp = start.getTimestamp();
			long endTimestamp = end.getTimestamp();

			int startBin = getDiscretizer().mapBin(startTimestamp);
			int endBin = getDiscretizer().mapBin(endTimestamp);

			for (int bin = startBin; bin <= endBin; bin++) {
				counts.get(SESSION).set(bin, counts.get(SESSION).get(bin) + 1);
			}
		}

		return counts;
	}

}
