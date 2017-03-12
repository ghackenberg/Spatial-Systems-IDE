package de.tum.imomesa.analyzer.aggregations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Cumulator;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.timelines.CsvSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.timelines.StackedAreaChartVisualization;
import de.tum.imomesa.analyzer.visualizations.timelines.StackedBarChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public abstract class TimelineAggregation<T> extends Aggregation<Map<T, List<Integer>>> {

	private Discretizer discretizer;

	public TimelineAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events);

		this.discretizer = discretizer;
	}

	protected Discretizer getDiscretizer() {
		return discretizer;
	}

	@Override
	protected List<Serialization<Map<T, List<Integer>>>> generateSerializations() throws IOException {
		List<T> order = Sorter.sortTimeline(getResult());

		List<Serialization<Map<T, List<Integer>>>> result = new ArrayList<>();

		Map<T, List<Integer>> cumulativeResult = Cumulator.cumulate(getResult());

		Writer writer = new FileWriter(Namer.convertUnderscore(getClass()) + ".csv");

		result.add(new CsvSerialization<>(getResult(), order, discretizer, writer));

		Writer writer_cum = new FileWriter(Namer.convertUnderscore(getClass()) + "_cumulative.csv");

		result.add(new CsvSerialization<>(cumulativeResult, order, discretizer, writer_cum));

		return result;
	}

	@Override
	protected List<Visualization<Map<T, List<Integer>>, ?>> generateVisualizations() {
		List<T> order = Sorter.sortTimeline(getResult());

		List<Visualization<Map<T, List<Integer>>, ?>> result = new ArrayList<>();

		Map<T, List<Integer>> cumulativeResult = Cumulator.cumulate(getResult());

		// result.add(new AreaChartVisualization<>(getResult(), discretizer));
		// result.add(new AreaChartVisualization<>(cumulativeResult,
		// discretizer));
		//
		// result.add(new BarChartVisualization<>(getResult(), discretizer));
		// result.add(new BarChartVisualization<>(cumulativeResult,
		// discretizer));

		result.add(new StackedAreaChartVisualization<>(getResult(), order, discretizer));
		result.add(new StackedAreaChartVisualization<>(cumulativeResult, order, discretizer));

		result.add(new StackedBarChartVisualization<>(getResult(), order, discretizer));
		result.add(new StackedBarChartVisualization<>(cumulativeResult, order, discretizer));

		return result;
	}

}
