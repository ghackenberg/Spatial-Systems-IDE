package de.tum.imomesa.analyzer.helpers.comparators;

import java.util.Comparator;
import java.util.Map;

public class StatisticsComparator<T, N extends Number> implements Comparator<T> {

	private Map<T, N> minimums;
	private Map<T, N> averages;
	// private Map<T, N> maximums;

	public StatisticsComparator(Map<T, N> minimums, Map<T, N> averages, Map<T, N> maximums) {
		this.minimums = minimums;
		this.averages = averages;
		// this.maximums = maximums;
	}

	@Override
	public int compare(T first, T second) {
		int value = (int) Math.signum(minimums.get(first).doubleValue() + averages.get(first).doubleValue()
				- minimums.get(second).doubleValue() - averages.get(second).doubleValue());

		if (value != 0) {
			return -value;
		} else {
			return -1;
		}
	}

}
