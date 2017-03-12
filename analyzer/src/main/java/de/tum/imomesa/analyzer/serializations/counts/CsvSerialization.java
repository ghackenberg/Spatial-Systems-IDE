package de.tum.imomesa.analyzer.serializations.counts;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;

public class CsvSerialization<T> extends Serialization<Map<T, Integer>> {

	private List<T> order;

	public CsvSerialization(Map<T, Integer> data, List<T> order, Writer writer) {
		super(data, writer);

		this.order = order;
	}

	@Override
	public void generateResult() throws IOException {
		getWriter().write("Object;Absolute frequency\n");

		for (T key : order) {
			Integer value = getData().get(key);
			getWriter().write(Namer.dispatch(key) + ";" + value + "\n");
		}

		getWriter().close();
	}

}
