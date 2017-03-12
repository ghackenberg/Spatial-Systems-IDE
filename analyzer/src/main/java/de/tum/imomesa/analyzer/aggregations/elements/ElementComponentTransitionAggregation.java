package de.tum.imomesa.analyzer.aggregations.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.TransitionAggregation;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ElementComponentTransitionAggregation extends TransitionAggregation<DefinitionComponent> {

	public ElementComponentTransitionAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);
	}

	@Override
	protected Map<DefinitionComponent, Map<DefinitionComponent, Integer>> generateResult() {

		// Initialize counts

		Map<DefinitionComponent, Map<DefinitionComponent, Integer>> counts = new HashMap<>();

		for (Change outerChange : getChanges()) {
			if (Mapper.mapFeature(outerChange) != null) {
				DefinitionComponent outerComponent = Mapper.mapComponent(outerChange);
				if (outerComponent != null && !counts.containsKey(outerComponent)) {
					counts.put(outerComponent, new HashMap<>());
					for (Change innerChange : getChanges()) {
						if (Mapper.mapFeature(innerChange) != null) {
							DefinitionComponent innerComponent = Mapper.mapComponent(innerChange);
							if (innerComponent != null && !outerComponent.equals(innerComponent)) {
								counts.get(outerComponent).put(innerComponent, 0);
							}
						}
					}
				}
			}
		}

		// Calculate counts and numbers

		DefinitionComponent previousComponent = null;

		for (Change change : getChanges()) {
			if (change instanceof ManageChange) {
				if (Mapper.mapFeature(change) != null) {
					DefinitionComponent currentComponent = Mapper.mapComponent(change);
					if (currentComponent != null) {
						if (previousComponent != null && !previousComponent.equals(currentComponent)) {
							counts.get(previousComponent).put(currentComponent,
									counts.get(previousComponent).get(currentComponent) + 1);
						}
						previousComponent = currentComponent;
					}
				}
			}
		}

		return counts;
	}

}
