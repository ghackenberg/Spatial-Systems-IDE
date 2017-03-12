package de.tum.imomesa.analyzer.helpers.comparators;

import java.util.Comparator;
import java.util.Map;

public class FrequencyComparator<T, N extends Number> implements Comparator<T> {

	private Map<T, N> frequencies;
	
	public FrequencyComparator(Map<T, N> frequencies) {
		this.frequencies = frequencies;
	}
	
	@Override
	public int compare(T first, T second) {
		return frequencies.get(second).intValue() - frequencies.get(first).intValue();
	}

}
