package de.tum.imomesa.analyzer.helpers;

import java.util.List;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public class Discretizer {

	private List<Change> changes;
	private List<Event> events;

	private Long minTimestamp;
	private Long maxTimestamp;

	private long milliseconds;
	private long seconds;
	private long minutes;
	private long hours;
	
	private long duration;
	private int bins;

	public Discretizer(List<Change> changes, List<Event> events, int bins_per_hour) {
		this.changes = changes;
		this.events = events;

		milliseconds = getMaximumTimestamp() - getMinimumTimestamp();
		seconds = (long) Math.ceil(milliseconds / 1000.0);
		minutes = (long) Math.ceil(seconds / 60.0);
		hours = (long) Math.ceil(minutes / 60.0);

		duration = hours * 60 * 60 * 1000;
		bins = (int) hours * bins_per_hour;
	}

	public List<Change> getChanges() {
		return changes;
	}

	public List<Event> getEvents() {
		return events;
	}

	public int getBins() {
		return bins;
	}

	public long getMinimumTimestamp() {
		if (minTimestamp == null) {
			long firstChangeTimestamp = changes.get(0).getTimestamp();
			long firstEventTimestamp = events.get(0).getTimestamp();

			minTimestamp = Math.min(firstChangeTimestamp, firstEventTimestamp);
		}

		return minTimestamp;
	}

	public long getMaximumTimestamp() {
		if (maxTimestamp == null) {
			long lastChangeTimestamp = changes.get(changes.size() - 1).getTimestamp();
			long lastEventTimestamp = events.get(events.size() - 1).getTimestamp();

			maxTimestamp = Math.max(lastChangeTimestamp, lastEventTimestamp);
		}

		return maxTimestamp;
	}

	public int mapBin(long timestamp) {
		long minimum = getMinimumTimestamp();

		double currentDiff = timestamp - minimum;

		return Math.min((int) Math.floor(currentDiff / duration * bins), bins - 1);
	}

	public double mapTime(int bin) {
		return (bin + 0.5) / bins * duration / 1000 / 60 / 60;
	}

}
