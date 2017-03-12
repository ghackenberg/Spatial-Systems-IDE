package de.tum.imomesa.analyzer.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Cumulator {

	public static <T> Map<T, List<Integer>> cumulate(Map<T, List<Integer>> data) {
		Map<T, List<Integer>> result = new HashMap<>();

		// Copy map
		for (Entry<T, List<Integer>> entry : data.entrySet()) {
			result.put(entry.getKey(), new ArrayList<>());

			for (Integer count : entry.getValue()) {
				result.get(entry.getKey()).add(count);
			}
		}

		// Cumulate counts
		for (Entry<T, List<Integer>> entry : result.entrySet()) {
			int previous = 0;

			for (int bin = 0; bin < entry.getValue().size(); bin++) {
				entry.getValue().set(bin, entry.getValue().get(bin) + previous);

				previous = entry.getValue().get(bin);
			}
		}

		return result;
	}

}
