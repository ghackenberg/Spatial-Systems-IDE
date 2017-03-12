package de.tum.imomesa.analyzer.aggregations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.durations.CsvSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.durations.StackedBarChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public abstract class DurationAggregation<T> extends Aggregation<DurationDescriptor<T>> {

	public DurationAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected List<Serialization<DurationDescriptor<T>>> generateSerializations() throws IOException {
		List<T> order = Sorter.sortDurations(getResult());

		Writer writer = new FileWriter(Namer.convertUnderscore(getClass()) + ".csv");

		List<Serialization<DurationDescriptor<T>>> result = new ArrayList<>();

		result.add(new CsvSerialization<>(getResult(), order, writer));

		return result;
	}

	@Override
	protected List<Visualization<DurationDescriptor<T>, ?>> generateVisualizations() {
		List<T> order = Sorter.sortDurations(getResult());

		List<Visualization<DurationDescriptor<T>, ?>> result = new ArrayList<>();

		result.add(new StackedBarChartVisualization<>(getResult(), order));

		return result;
	}

}
