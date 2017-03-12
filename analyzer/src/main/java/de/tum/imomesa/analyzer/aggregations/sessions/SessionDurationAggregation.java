package de.tum.imomesa.analyzer.aggregations.sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.DurationAggregation;
import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.workbench.commons.events.ProgramEndEvent;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;

public class SessionDurationAggregation extends DurationAggregation<String> {

	private static final String SESSION = "Session";

	public SessionDurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected DurationDescriptor<String> generateResult() {
		Map<String, List<Long>> counts = new HashMap<>();

		counts.put(SESSION, new ArrayList<>());

		ProgramStartEvent start = null;
		ProgramEndEvent end = null;

		for (Event event : getEvents()) {
			if (event instanceof ProgramStartEvent) {
				start = (ProgramStartEvent) event;
			} else if (event instanceof ProgramEndEvent) {
				end = (ProgramEndEvent) event;

				counts.get(SESSION).add(end.getTimestamp() - start.getTimestamp());
			}
		}

		return new DurationDescriptor<>(counts);
	}

}
