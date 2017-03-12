package de.tum.imomesa.analyzer.serializations.timelines;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Formatter;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;

public class CsvSerialization<T> extends Serialization<Map<T, List<Integer>>> {

	private List<T> order;
	private Discretizer discretizer;

	public CsvSerialization(Map<T, List<Integer>> data, List<T> order, Discretizer discretizer, Writer writer) {
		super(data, writer);

		this.order = order;
		this.discretizer = discretizer;
	}

	@Override
	public void generateResult() throws IOException {
		getWriter().write("Object");

		for (int bin = 0; bin < discretizer.getBins(); bin++) {
			getWriter().write(";" + Formatter.FORMAT_GERMAN.format(discretizer.mapTime(bin)));
		}

		getWriter().write("\n");

		for (T key : order) {
			getWriter().write(Namer.dispatch(key));

			List<Integer> value = getData().get(key);

			for (int bin = 0; bin < discretizer.getBins(); bin++) {
				getWriter().write(";" + (value != null ? value.get(bin) : 0));
			}

			getWriter().write("\n");
		}

		getWriter().close();
	}

}
