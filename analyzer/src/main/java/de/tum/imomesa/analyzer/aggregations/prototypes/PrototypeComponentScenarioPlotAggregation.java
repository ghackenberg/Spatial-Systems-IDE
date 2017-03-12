package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.descriptors.EventDescriptor;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.prototypes.HtmlMultiSwimlaneSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.generics.WebViewVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class PrototypeComponentScenarioPlotAggregation
		extends Aggregation<Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>>> {

	private static final String MANAGE = "Manage";
	private static final String RELEASE = "Release";

	private Discretizer discretizer;

	private String file;

	public PrototypeComponentScenarioPlotAggregation(List<Change> changes, List<Event> events,
			Discretizer discretizer) {
		super(changes, events);

		this.discretizer = discretizer;

		file = Namer.convertUnderscore(getClass()) + ".html";
	}

	@Override
	protected Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> generateResult() {
		Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>> result = new HashMap<>();

		// Manage events
		for (Change change : getChanges()) {
			if (change instanceof ManageChange) {
				ManageChange manage = (ManageChange) change;

				if (manage.getObject() instanceof Scenario) {
					Scenario scenario = (Scenario) manage.getObject();

					List<DefinitionComponent> components = scenario.getAncestorsByType(DefinitionComponent.class);

					DefinitionComponent firstComponent = components.get(0);
					DefinitionComponent lastComponent = components.get(components.size() - 1);

					Project project = lastComponent.getFirstAncestorByType(Project.class);

					if (project.getComponent() == lastComponent) {
						if (!result.containsKey(firstComponent)) {
							result.put(firstComponent, new HashMap<>());
						}

						if (!result.get(firstComponent).containsKey(scenario)) {
							result.get(firstComponent).put(scenario, new ArrayList<>());
						}

						result.get(firstComponent).get(scenario)
								.add(new EventDescriptor(change.getTimestamp(), MANAGE, "Element created."));
					}
				}
			}
		}

		// Simulation events
		StartEvent start = null;

		Set<String> messages = new HashSet<>();

		String description = "";

		for (Event event : getEvents()) {
			if (event instanceof StartEvent) {
				start = (StartEvent) event;

				messages.clear();

				description = "";
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent casted = (SimulationMarkerEvent) event;

				SimulationMarker marker = casted.getMarker();

				String name = Namer.dispatch(marker);
				
				if (name != null) {
					messages.add(name.replace(' ', '_'));
				}

				description += (description.length() > 0 ? "\n" : "") + marker.getMessage();
			} else if (event instanceof EndEvent) {
				Scenario scenario = start.getScenario();

				DefinitionComponent component = scenario.getFirstAncestorByType(DefinitionComponent.class);

				if (result.containsKey(component)) {
					String message = "";

					for (String temp : messages) {
						message += (message.length() > 0 ? " " : "") + temp;
					}

					result.get(component).get(scenario)
							.add(new EventDescriptor(start.getTimestamp(), message, description));
				}
			}
		}

		// Release events
		for (Change change : getChanges()) {
			if (change instanceof ReleaseChange) {
				ReleaseChange release = (ReleaseChange) change;

				if (release.getObject() instanceof Scenario) {
					Scenario scenario = (Scenario) release.getObject();

					DefinitionComponent component = scenario.getFirstAncestorByType(DefinitionComponent.class);

					if (result.containsKey(component)) {
						result.get(component).get(scenario)
								.add(new EventDescriptor(change.getTimestamp(), RELEASE, "Element deleted."));
					}
				}
			}
		}

		// Clean scenarios
		for (DefinitionComponent component : result.keySet()) {
			List<Scenario> remove = new ArrayList<>();

			for (Scenario scenario : result.get(component).keySet()) {
				boolean sim = false;

				for (EventDescriptor event : result.get(component).get(scenario)) {
					if (!event.getMessage().equals(MANAGE) && !event.getMessage().equals(RELEASE)) {
						sim = true;
						break;
					}
				}

				if (!sim) {
					remove.add(scenario);
				}
			}

			for (Scenario scenario : remove) {
				result.get(component).remove(scenario);
			}
		}

		// Clean components
		List<DefinitionComponent> remove = new ArrayList<>();

		for (DefinitionComponent component : result.keySet()) {
			if (result.get(component).size() == 0) {
				remove.add(component);
			}
		}

		for (DefinitionComponent component : remove) {
			result.remove(component);
		}

		return result;
	}

	@Override
	protected List<Serialization<Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>>>> generateSerializations()
			throws IOException {
		List<DefinitionComponent> outerOrder = Sorter.sortEventDescriptorsComponents(getResult());
		List<Scenario> innerOrder = Sorter.sortEventDescriptorsScenarios(getResult());

		List<Serialization<Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>>>> result = new ArrayList<>();

		Writer writer = new FileWriter(file);

		result.add(new HtmlMultiSwimlaneSerialization<>(getResult(), outerOrder, innerOrder, writer, discretizer));

		return result;
	}

	@Override
	protected List<Visualization<Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>>, ?>> generateVisualizations() {
		List<Visualization<Map<DefinitionComponent, Map<Scenario, List<EventDescriptor>>>, ?>> result = new ArrayList<>();

		result.add(new WebViewVisualization<>(getResult(), new File(file)));

		return result;
	}

}
