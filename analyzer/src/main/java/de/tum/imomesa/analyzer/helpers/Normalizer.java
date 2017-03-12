package de.tum.imomesa.analyzer.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.workbench.commons.events.ProgramEndEvent;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;

public class Normalizer {

	public static void normalize(List<Change> changes, List<Event> events) {
		int sessions = 0;

		long last = 0; //events.get(0).getTimestamp();

		for (Event event : events) {
			if (event instanceof ProgramStartEvent) {
				if (sessions == 0) {
					long difference = event.getTimestamp() - last;

					System.out.println("Substracting " + difference);

					for (Event nested : events) {
						if (nested.getTimestamp() > last) {
							nested.setTimestamp(nested.getTimestamp() - difference);
						}
					}
					for (Change change : changes) {
						if (change.getTimestamp() > last) {
							change.setTimesamp(change.getTimestamp() - difference);
						}
					}
				}

				sessions++;
			}
			if (event instanceof ProgramEndEvent) {
				sessions--;

				if (sessions == 0) {
					last = event.getTimestamp();
				}
			}
		}
	}

	public static <T> Map<T, Map<T, Double>> normalize(Map<T, Map<T, Integer>> data) {
		// Calculate frequencies
		Map<T, Map<T, Double>> frequencies = new HashMap<>();

		for (Entry<T, Map<T, Integer>> outerEntry : data.entrySet()) {
			int sum = 0;

			for (Entry<T, Integer> innerEntry : outerEntry.getValue().entrySet()) {
				sum += innerEntry.getValue();
			}

			frequencies.put(outerEntry.getKey(), new HashMap<>());

			for (Entry<T, Integer> innerEntry : outerEntry.getValue().entrySet()) {
				frequencies.get(outerEntry.getKey()).put(innerEntry.getKey(), innerEntry.getValue() * 1.0 / sum);
			}
		}

		return frequencies;
	}

}
