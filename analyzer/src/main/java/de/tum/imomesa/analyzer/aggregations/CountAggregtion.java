package de.tum.imomesa.analyzer.aggregations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.counts.CsvSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.counts.PieChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public abstract class CountAggregtion<T> extends Aggregation<Map<T, Integer>> {

	public CountAggregtion(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected List<Serialization<Map<T, Integer>>> generateSerializations() throws IOException {
		List<T> order = Sorter.sortCounts(getResult());

		Writer writer = new FileWriter(Namer.convertUnderscore(getClass()) + ".csv");

		List<Serialization<Map<T, Integer>>> result = new ArrayList<>();

		result.add(new CsvSerialization<>(getResult(), order, writer));

		return result;
	}

	@Override
	protected List<Visualization<Map<T, Integer>, ?>> generateVisualizations() {
		List<T> order = Sorter.sortCounts(getResult());

		List<Visualization<Map<T, Integer>, ?>> result = new ArrayList<>();

		result.add(new PieChartVisualization<>(getResult(), order));

		return result;
	}

}
