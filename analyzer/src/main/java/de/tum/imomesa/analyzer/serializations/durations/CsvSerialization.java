package de.tum.imomesa.analyzer.serializations.durations;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.helpers.Formatter;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;

public class CsvSerialization<T> extends Serialization<DurationDescriptor<T>> {

	private List<T> order;

	public CsvSerialization(DurationDescriptor<T> data, List<T> order, Writer writer) {
		super(data, writer);

		this.order = order;
	}

	@Override
	public void generateResult() throws IOException {
		getWriter().write(
				"Object;Minimum duration (in minutes);Average duration (in minutes);Maximum duration (in minutes)\n");

		for (T key : order) {
			getWriter().write(Namer.dispatch(key));

			Double minimum = getData().getSortedMinimums().get(key);
			Double average = getData().getSortedAverages().get(key);
			Double maximum = getData().getSortedMaximums().get(key);

			getWriter().write(";" + Formatter.FORMAT_GERMAN.format(minimum / 1000 / 60));
			getWriter().write(";" + Formatter.FORMAT_GERMAN.format(average / 1000 / 60));
			getWriter().write(";" + Formatter.FORMAT_GERMAN.format(maximum / 1000 / 60));

			getWriter().write("\n");
		}

		getWriter().close();
	}

}
