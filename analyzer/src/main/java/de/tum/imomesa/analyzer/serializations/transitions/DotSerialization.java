package de.tum.imomesa.analyzer.serializations.transitions;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.helpers.Formatter;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Normalizer;
import de.tum.imomesa.analyzer.serializations.Serialization;

public class DotSerialization<T> extends Serialization<Map<T, Map<T, Integer>>> {

	public DotSerialization(Map<T, Map<T, Integer>> data, Writer writer) {
		super(data, writer);
	}

	@Override
	public void generateResult() throws IOException {
		Map<T, Map<T, Double>> frequencies = Normalizer.normalize(getData());

		double max = 0;

		for (Entry<T, Map<T, Double>> outerEntry : frequencies.entrySet()) {
			for (Entry<T, Double> innerEntry : outerEntry.getValue().entrySet()) {
				if (max < innerEntry.getValue()) {
					max = innerEntry.getValue();
					System.out.println(
							outerEntry.getKey() + " -> " + innerEntry.getKey() + " = " + innerEntry.getValue());
				}
			}
		}

		System.out.println("MAX: " + max);

		// Graph start
		getWriter().write("digraph G {\n");
		getWriter().write("size = \"12,12\";\n");
		getWriter().write("ratio = \"fill\";\n");

		// Node
		getWriter().write("node [");
		getWriter().write("fontname = \"Calibri\", fontsize = 40,");
		getWriter().write("penwidth = 5, margin = 0.25,");
		getWriter().write("shape = rectangle, style = filled,");
		getWriter().write("color = \"black\", fillcolor = \"lightgray\", fontcolor = \"black\"];\n");

		// Edge
		getWriter().write("edge [fontname = \"Calibri\", fontsize = 40];\n");

		// Content
		for (Entry<T, Map<T, Double>> outerEntry : frequencies.entrySet()) {
			getWriter().write("\"" + Namer.dispatch(outerEntry.getKey()) + "\";\n");
			for (Entry<T, Double> innerEntry : outerEntry.getValue().entrySet()) {
				if (innerEntry.getValue() > 0) {
					getWriter().write("\"" + Namer.dispatch(outerEntry.getKey()) + "\" -> \""
							+ Namer.dispatch(innerEntry.getKey()) + "\" [label = \""
							+ Formatter.FORMAT_MINIMAL.format(innerEntry.getValue() * 100) + "%\", penwidth = "
							+ (5 + innerEntry.getValue() * 20) + ", color = \""
							+ toColorString(innerEntry.getValue(), max) + "\", fontcolor = \""
							+ toColorString(innerEntry.getValue(), max) + "\"];\n");
				}
			}
		}

		// Graph end
		getWriter().write("}");
		getWriter().close();
	}

	protected String toColorString(double probability, double max) {
		return toColorString(probability / max * 255, 0, (1 - probability / max) * 255);
	}

	protected String toColorString(double red, double green, double blue) {
		return toColorString((int) Math.round(red), (int) Math.round(green), (int) Math.round(blue));
	}

	protected String toColorString(int red, int green, int blue) {
		String redString = Integer.toHexString(red);
		String greenString = Integer.toHexString(green);
		String blueString = Integer.toHexString(blue);

		String color = "#" + (redString.length() == 1 ? "0" : "") + redString + (greenString.length() == 1 ? "0" : "")
				+ greenString + (blueString.length() == 1 ? "0" : "") + blueString;

		return color;
	}

}
