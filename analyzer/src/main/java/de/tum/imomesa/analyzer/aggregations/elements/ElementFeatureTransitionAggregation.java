package de.tum.imomesa.analyzer.aggregations.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TransitionAggregation;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ElementFeatureTransitionAggregation extends TransitionAggregation<Class<?>> {

	public ElementFeatureTransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<Class<?>, Map<Class<?>, Integer>> generateResult() {
		Map<Class<?>, Map<Class<?>, Integer>> counts = new HashMap<>();

		for (Class<?> outer : Mapper.FEATURES_INCLUDED) {
			counts.put(outer, new HashMap<>());
			for (Class<?> inner : Mapper.FEATURES_INCLUDED) {
				if (!outer.equals(inner)) {
					counts.get(outer).put(inner, 0);
				}
			}
		}

		Map<DefinitionComponent, Class<?>> previousTypes = new HashMap<>();

		for (Change change : getChanges()) {
			if (change instanceof ManageChange) {
				Class<?> currentType = Mapper.mapFeature(change);
				if (currentType != null) {
					Element currentElement = ((Element) change.getObject());
					DefinitionComponent currentContext = currentElement.getParent()
							.getFirstAncestorByType(DefinitionComponent.class);
					if (currentContext != null) {
						Class<?> previousType = previousTypes.get(currentContext);
						if (previousType != null && !previousType.equals(currentType)) {
							counts.get(previousType).put(currentType, counts.get(previousType).get(currentType) + 1);
						}
						previousTypes.put(currentContext, currentType);
					}
				}
			}
		}

		return counts;
	}

}
