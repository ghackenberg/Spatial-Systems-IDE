package de.tum.imomesa.analyzer.aggregations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.transitions.DotSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.generics.GraphVizVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;

public abstract class TransitionAggregation<T> extends Aggregation<Map<T, Map<T, Integer>>> {

	private String file;

	public TransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);

		file = Namer.convertUnderscore(getClass()) + ".dot";
	}

	@Override
	protected List<Serialization<Map<T, Map<T, Integer>>>> generateSerializations() throws IOException {
		List<Serialization<Map<T, Map<T, Integer>>>> result = new ArrayList<>();

		result.add(new DotSerialization<>(getResult(), new FileWriter(file)));

		return result;
	}

	@Override
	protected List<Visualization<Map<T, Map<T, Integer>>, ?>> generateVisualizations() {
		List<Visualization<Map<T, Map<T, Integer>>, ?>> result = new ArrayList<>();

		result.add(new GraphVizVisualization<>(getResult(), new File(file)));

		return result;
	}

}
