package de.tum.imomesa.analyzer.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.descriptors.DurationDescriptor;
import de.tum.imomesa.analyzer.descriptors.EventDescriptor;
import de.tum.imomesa.analyzer.descriptors.SimulationDescriptor;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;

public class Sorter {

	public static <T> List<T> sortCounts(Map<T, Integer> counts) {
		List<T> result = new ArrayList<>();

		for (Entry<T, Integer> count : counts.entrySet()) {
			result.add(count.getKey());
		}

		Collections.sort(result, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return Math.abs(counts.get(o2)) - Math.abs(counts.get(o1));
			}
		});

		return result;
	}

	public static <T> List<T> sortTimeline(Map<T, List<Integer>> timeline) {
		Map<T, Integer> counts = new HashMap<>();

		for (Entry<T, List<Integer>> entry : timeline.entrySet()) {
			int sum = 0;

			for (Integer count : entry.getValue()) {
				sum += count;
			}

			counts.put(entry.getKey(), sum);
		}

		return sortCounts(counts);
	}

	public static <T1, T2> List<T1> sortNestedTimeline(Map<T1, Map<T2, List<Integer>>> timeline) {
		Map<T1, Integer> counts = new HashMap<>();

		for (Entry<T1, Map<T2, List<Integer>>> outerEntry : timeline.entrySet()) {
			int sum = 0;

			for (Entry<T2, List<Integer>> innerEntry : outerEntry.getValue().entrySet()) {
				for (Integer count : innerEntry.getValue()) {
					sum += count;
				}
			}

			counts.put(outerEntry.getKey(), sum);
		}

		return sortCounts(counts);
	}

	public static <T> List<T> sortDurations(DurationDescriptor<T> durations) {
		List<T> result = new ArrayList<>();

		for (Entry<T, Double> entry : durations.getSortedAverages().entrySet()) {
			result.add(entry.getKey());
		}

		Collections.sort(result, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				double first = Math.abs(durations.getSortedMinimums().get(o2) + durations.getSortedAverages().get(o2));
				double second = Math.abs(durations.getSortedMinimums().get(o1) + durations.getSortedAverages().get(o1));

				double diff = first - second;
				
				return (int) Math.signum(diff);
			}
		});

		return result;
	}

	public static List<DefinitionComponent> sortSimulationDescriptorComponents(List<SimulationDescriptor> descriptors) {
		List<DefinitionComponent> result = new ArrayList<>();

		for (SimulationDescriptor descriptor : descriptors) {
			Scenario scenario = descriptor.getScenario();

			DefinitionComponent component = scenario.getFirstAncestorByType(DefinitionComponent.class);

			if (!result.contains(component)) {
				result.add(component);
			}
		}

		return result;
	}

	public static List<Scenario> sortSimulationDescriptorScenarios(List<SimulationDescriptor> descriptors) {
		List<Scenario> result = new ArrayList<>();

		for (SimulationDescriptor descriptor : descriptors) {
			Scenario scenario = descriptor.getScenario();

			if (!result.contains(scenario)) {
				result.add(scenario);
			}
		}

		return result;
	}

	public static <T> List<T> sortTimestamps(Map<T, Long> timestamps) {
		List<T> result = new ArrayList<>();

		for (Entry<T, Long> entry : timestamps.entrySet()) {
			result.add(entry.getKey());
		}

		Collections.sort(result, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				double diff = Math.abs(timestamps.get(o1)) - Math.abs(timestamps.get(o2));
				return (int) Math.signum(diff);
			}
		});

		return result;
	}

	public static List<DefinitionComponent> sortEventDescriptors(
			Map<DefinitionComponent, List<EventDescriptor>> events) {
		Map<DefinitionComponent, Long> timestamps = new HashMap<>();

		for (Entry<DefinitionComponent, List<EventDescriptor>> entry : events.entrySet()) {
			timestamps.put(entry.getKey(), Long.MAX_VALUE);

			for (EventDescriptor descriptor : entry.getValue()) {
				if (descriptor.getTimestamp() < timestamps.get(entry.getKey())) {
					timestamps.put(entry.getKey(), descriptor.getTimestamp());
				}
			}
		}

		return sortTimestamps(timestamps);
	}

	public static List<DefinitionComponent> sortEventDescriptorsComponents(
			Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> events) {
		Map<DefinitionComponent, Long> timestamps = new HashMap<>();

		for (Entry<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> outerEntry : events.entrySet()) {
			timestamps.put(outerEntry.getKey(), Long.MAX_VALUE);

			for (Entry<Scenario, List<EventDescriptor>> innerEntry : outerEntry.getValue().entrySet()) {
				for (EventDescriptor descriptor : innerEntry.getValue()) {
					if (descriptor.getTimestamp() < timestamps.get(outerEntry.getKey())) {
						timestamps.put(outerEntry.getKey(), descriptor.getTimestamp());
					}
				}
			}
		}

		return sortTimestamps(timestamps);
	}

	public static List<Scenario> sortEventDescriptorsScenarios(
			Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> events) {
		Map<Scenario, Long> timestamps = new HashMap<>();

		for (Entry<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> outerEntry : events.entrySet()) {
			for (Entry<Scenario, List<EventDescriptor>> innerEntry : outerEntry.getValue().entrySet()) {
				timestamps.put(innerEntry.getKey(), Long.MAX_VALUE);

				for (EventDescriptor descriptor : innerEntry.getValue()) {
					if (descriptor.getTimestamp() < timestamps.get(innerEntry.getKey())) {
						timestamps.put(innerEntry.getKey(), descriptor.getTimestamp());
					}
				}
			}
		}

		return sortTimestamps(timestamps);
	}

	public static List<Scenario> sortScenarioRuns(Map<Scenario, Map<Long, String>> runs) {
		Map<Scenario, Long> timestamps = new HashMap<>();

		for (Entry<Scenario, Map<Long, String>> outerEntry : runs.entrySet()) {
			timestamps.put(outerEntry.getKey(), Long.MAX_VALUE);

			for (Entry<Long, String> innerEntry : outerEntry.getValue().entrySet()) {
				if (innerEntry.getKey() < timestamps.get(outerEntry.getKey())) {
					timestamps.put(outerEntry.getKey(), innerEntry.getKey());
				}
			}
		}

		return sortTimestamps(timestamps);
	}

}
