package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.descriptors.EventDescriptor;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Mapper;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.prototypes.HtmlSwimlaneSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.generics.WebViewVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.DefinitionComponent;

public class PrototypeComponentFeaturePlotAggregation
		extends Aggregation<Map<DefinitionComponent, List<EventDescriptor>>> {

	private Discretizer discretizer;

	private String file;

	public PrototypeComponentFeaturePlotAggregation(List<Change> changes, List<Event> events, Discretizer discretizer) {
		super(changes, events);

		this.discretizer = discretizer;

		file = Namer.convertUnderscore(getClass()) + ".html";
	}

	@Override
	protected Map<DefinitionComponent, List<EventDescriptor>> generateResult() {
		Map<DefinitionComponent, List<EventDescriptor>> result = new HashMap<>();

		DefinitionComponent lastComponent = null;
		Class<?> lastFeature = null;

		for (Change change : getChanges()) {
			DefinitionComponent currentComponent = Mapper.mapComponent(change);
			Class<?> currentFeature = Mapper.mapFeature(change);

			if (currentComponent != null && currentFeature != null) {
				List<DefinitionComponent> components = currentComponent
						.getAncestorsOrSelfByType(DefinitionComponent.class);

				DefinitionComponent rootComponent = components.get(components.size() - 1);

				Project project = rootComponent.getFirstAncestorByType(Project.class);

				if (project.getComponent() == rootComponent && !result.containsKey(currentComponent)) {
					result.put(currentComponent, new ArrayList<>());
				}

				if (lastComponent != null && lastFeature != null) {
					if (result.containsKey(lastComponent) && lastComponent != currentComponent) {
						result.get(lastComponent).add(new EventDescriptor(change.getTimestamp(), "Idle",
								"Switching to component: " + currentComponent.getName()));
					}
				}

				if (result.containsKey(currentComponent)) {
					if (lastFeature == null || lastComponent != currentComponent || lastFeature != currentFeature) {
						String message = currentFeature.getSimpleName();
						String description = message;

						result.get(currentComponent)
								.add(new EventDescriptor(change.getTimestamp(), message, description));
					}
				}

				lastFeature = currentFeature;
				lastComponent = currentComponent;
			}
		}

		return result;

	}

	@Override
	protected List<Serialization<Map<DefinitionComponent, List<EventDescriptor>>>> generateSerializations()
			throws IOException {
		List<DefinitionComponent> order = Sorter.sortEventDescriptors(getResult());

		List<Serialization<Map<DefinitionComponent, List<EventDescriptor>>>> result = new ArrayList<>();

		result.add(new HtmlSwimlaneSerialization<>(getResult(), order, new FileWriter(file), discretizer));

		return result;
	}

	@Override
	protected List<Visualization<Map<DefinitionComponent, List<EventDescriptor>>, ?>> generateVisualizations() {
		List<Visualization<Map<DefinitionComponent, List<EventDescriptor>>, ?>> result = new ArrayList<>();

		result.add(new WebViewVisualization<>(getResult(), new File(file)));

		return result;
	}

}
