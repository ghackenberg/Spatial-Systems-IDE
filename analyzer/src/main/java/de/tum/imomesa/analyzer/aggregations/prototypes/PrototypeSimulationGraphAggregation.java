package de.tum.imomesa.analyzer.aggregations.prototypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.analyzer.aggregations.Aggregation;
import de.tum.imomesa.analyzer.descriptors.SimulationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.helpers.Sorter;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.analyzer.serializations.prototypes.DotSerialization;
import de.tum.imomesa.analyzer.serializations.prototypes.HtmlTableSerialization;
import de.tum.imomesa.analyzer.visualizations.Visualization;
import de.tum.imomesa.analyzer.visualizations.generics.GraphVizVisualization;
import de.tum.imomesa.analyzer.visualizations.generics.WebViewVisualization;
import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.SimulationMarkerEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;

public class PrototypeSimulationGraphAggregation extends Aggregation<List<SimulationDescriptor>> {

	private static final String FAILURE = "Failure";
	private static final String TIMEOUT = "Timeout";
	private static final String SUCCESS = "Success";

	private String dotFile;
	private String htmlFile;

	public PrototypeSimulationGraphAggregation(List<Change> changes, List<Event> events) {
		super(changes, events);

		dotFile = Namer.convertUnderscore(getClass()) + ".dot";
		htmlFile = Namer.convertUnderscore(getClass()) + ".html";
	}

	@Override
	protected List<SimulationDescriptor> generateResult() {
		List<SimulationDescriptor> descriptors = new ArrayList<>();

		Scenario scenario = null;

		boolean failure = false;
		boolean timeout = false;

		for (Event event : getEvents()) {
			if (event instanceof StartEvent) {
				StartEvent start = (StartEvent) event;

				scenario = start.getScenario();

				failure = false;
				timeout = false;
			} else if (event instanceof SimulationMarkerEvent) {
				SimulationMarkerEvent casted = (SimulationMarkerEvent) event;

				SimulationMarker marker = casted.getMarker();

				if (marker instanceof TimeoutMarker) {
					timeout = true;
				} else if (marker instanceof ErrorMarker) {
					failure = true;
				}
			} else if (event instanceof EndEvent) {
				String result = null;

				if (failure) {
					result = FAILURE;
				} else if (timeout) {
					result = TIMEOUT;
				} else {
					result = SUCCESS;
				}

				if (descriptors.size() == 0) {
					descriptors.add(new SimulationDescriptor(scenario, result));
				} else {
					SimulationDescriptor last = descriptors.get(descriptors.size() - 1);

					if (last.getScenario().equals(scenario) && last.getResult().equals(result)) {
						last.incrementCount();
					} else {
						descriptors.add(new SimulationDescriptor(scenario, result));
					}
				}
			}
		}

		return descriptors;
	}

	@Override
	protected List<Serialization<List<SimulationDescriptor>>> generateSerializations() throws IOException {
		List<DefinitionComponent> outerOrder = Sorter.sortSimulationDescriptorComponents(getResult());
		List<Scenario> innerOrder = Sorter.sortSimulationDescriptorScenarios(getResult());

		List<Serialization<List<SimulationDescriptor>>> result = new ArrayList<>();

		result.add(new DotSerialization(getResult(), new FileWriter(dotFile)));
		result.add(new HtmlTableSerialization(getResult(), outerOrder, innerOrder, new FileWriter(htmlFile)));

		return result;
	}

	@Override
	protected List<Visualization<List<SimulationDescriptor>, ?>> generateVisualizations() {
		List<Visualization<List<SimulationDescriptor>, ?>> result = new ArrayList<>();

		result.add(new GraphVizVisualization<>(getResult(), new File(dotFile)));
		result.add(new WebViewVisualization<>(getResult(), new File(htmlFile)));

		return result;
	}

}
