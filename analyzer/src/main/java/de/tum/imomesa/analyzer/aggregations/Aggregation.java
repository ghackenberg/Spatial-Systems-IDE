package de.tum.imomesa.analyzer.aggregations;

import java.io.IOException;
import java.util.List;

import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public abstract class Aggregation<T> {

	private List<Change> changes;
	private List<Event> events;
	private T result;
	private List<Serialization<T>> serializations;
	private List<Visualization<T, ?>> visualizations;

	public Aggregation(List<Change> changes, List<Event> events) {
		this.changes = changes;
		this.events = events;
	}

	protected List<Change> getChanges() {
		return changes;
	}

	protected List<Event> getEvents() {
		return events;
	}

	protected abstract T generateResult();

	protected T getResult() {
		if (result == null) {
			result = generateResult();
		}
		return result;
	}

	protected abstract List<Serialization<T>> generateSerializations() throws IOException;

	public List<Serialization<T>> getSerializations() throws IOException {
		if (serializations == null) {
			serializations = generateSerializations();
		}
		return serializations;
	}

	protected abstract List<Visualization<T, ?>> generateVisualizations();

	public List<Visualization<T, ?>> getVisualizations() {
		if (visualizations == null) {
			visualizations = generateVisualizations();
		}
		return visualizations;
	}

}
