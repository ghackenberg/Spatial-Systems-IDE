package de.tum.imomesa.analyzer.descriptors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DurationDescriptor<T> extends Descriptor {

	private Map<T, Double> sortedMinimums;
	private Map<T, Double> sortedAverages;
	private Map<T, Double> sortedMaximums;

	public DurationDescriptor(Map<T, List<Long>> durations) {
		Map<T, Double> minimums = new HashMap<>();
		Map<T, Double> averages = new HashMap<>();
		Map<T, Double> maximums = new HashMap<>();

		for (Entry<T, List<Long>> entry : durations.entrySet()) {
			double minimum = Long.MAX_VALUE;
			double average = 0;
			double maximum = Long.MIN_VALUE;

			for (double duration : entry.getValue()) {
				minimum = Math.min(minimum, duration);
				average += duration / entry.getValue().size();
				maximum = Math.max(maximum, duration);
			}

			minimums.put(entry.getKey(), minimum);
			averages.put(entry.getKey(), average - minimum);
			maximums.put(entry.getKey(), maximum - average);
		}

		sortedMinimums = minimums;
		sortedAverages = averages;
		sortedMaximums = maximums;

		/*
		 * sortedMinimums = new TreeMap<>(new StatisticsComparator<>(minimums,
		 * averages, maximums)); sortedAverages = new TreeMap<>(new
		 * StatisticsComparator<>(minimums, averages, maximums)); sortedMaximums
		 * = new TreeMap<>(new StatisticsComparator<>(minimums, averages,
		 * maximums));
		 * 
		 * sortedMinimums.putAll(minimums); sortedAverages.putAll(averages);
		 * sortedMaximums.putAll(maximums);
		 */
	}

	public Map<T, Double> getSortedMinimums() {
		return sortedMinimums;
	}

	public Map<T, Double> getSortedAverages() {
		return sortedAverages;
	}

	public Map<T, Double> getSortedMaximums() {
		return sortedMaximums;
	}

}
