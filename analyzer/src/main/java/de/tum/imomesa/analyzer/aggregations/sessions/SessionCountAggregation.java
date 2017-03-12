package de.tum.imomesa.analyzer.aggregations.sessions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.CountAggregtion;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;

public class SessionCountAggregation extends CountAggregtion<String> {

	private static final String SESSION = "Session";

	public SessionCountAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<String, Integer> generateResult() {
		Map<String, Integer> counts = new HashMap<>();

		counts.put(SESSION, 0);

		for (Event event : getEvents()) {
			if (event instanceof ProgramStartEvent) {
				counts.put(SESSION, counts.get(SESSION) + 1);
			}
		}

		return counts;
	}

}
