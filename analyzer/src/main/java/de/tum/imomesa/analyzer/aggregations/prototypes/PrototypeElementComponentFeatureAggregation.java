package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.prototypes.MultiStackedAreaChartVisualization;
import de.tum.imomesa.analyzer.visualizations.prototypes.MultiStackedBarChartVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;

public class PrototypeElementComponentFeatureAggregation
		extends Aggregation<Map<DefinitionComponent, Map<Class<?>, List<Integer>>>> {

	private Discretizer discretizer;

	public PrototypeElementComponentFeatureAggregation(List<Change> changes, List<Event> events,
			Discretizer discretizer) {
		super(changes, events);

		this.discretizer = discretizer;
	}

	@Override
	protected Map<DefinitionComponent, Map<Class<?>, List<Integer>>> generateResult() {
		Map<DefinitionComponent, Map<Class<?>, List<Integer>>> result = new HashMap<>();

		for (Change change : getChanges()) {
			DefinitionComponent component = Mapper.mapComponent(change);

			if (component != null) {
				if (!result.containsKey(component)) {
					result.put(component, new HashMap<>());

					for (Class<?> feature : Mapper.FEATURES_INCLUDED) {
						result.get(component).put(feature, new ArrayList<>());

						for (int bin = 0; bin < discretizer.getBins(); bin++) {
							result.get(component).get(feature).add(0);
						}
					}
				}

				Class<?> feature = Mapper.mapFeature(change);

				if (feature != null) {
					int bin = discretizer.mapBin(change.getTimestamp());

					result.get(component).get(feature).set(bin, result.get(component).get(feature).get(bin) + 1);
				}
			}
		}

		return result;
	}

	@Override
	protected List<Serialization<Map<DefinitionComponent, Map<Class<?>, List<Integer>>>>> generateSerializations()
			throws IOException {
		List<Serialization<Map<DefinitionComponent, Map<Class<?>, List<Integer>>>>> result = new ArrayList<>();

		return result;
	}

	@Override
	protected List<Visualization<Map<DefinitionComponent, Map<Class<?>, List<Integer>>>, ?>> generateVisualizations() {
		List<DefinitionComponent> outerOrder = Sorter.sortNestedTimeline(getResult());

		DefinitionComponent first = outerOrder.get(0);

		List<Class<?>> innerOrder = Sorter.sortTimeline(getResult().get(first));

		List<Visualization<Map<DefinitionComponent, Map<Class<?>, List<Integer>>>, ?>> result = new ArrayList<>();

		// result.add(new MultiBarChartVisualization<>(getResult(),
		// discretizer));
		// result.add(new MultiAreaChartVisualization<>(getResult(),
		// discretizer));
		result.add(new MultiStackedBarChartVisualization<>(getResult(), outerOrder, innerOrder, discretizer));
		result.add(new MultiStackedAreaChartVisualization<>(getResult(), outerOrder, innerOrder, discretizer));

		return result;
	}

}
