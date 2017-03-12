package de.tum.imomesa.analyzer.serializations.prototypes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tum.imomesa.analyzer.descriptors.SimulationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;

public class DotSerialization extends Serialization<List<SimulationDescriptor>> {

	public DotSerialization(List<SimulationDescriptor> data, Writer writer) {
		super(data, writer);
	}

	@Override
	public void generateResult() throws IOException {
		Map<DefinitionComponent, Map<Scenario, List<SimulationDescriptor>>> swimlanes = new HashMap<>();

		for (SimulationDescriptor descriptor : getData()) {
			Scenario scenario = descriptor.getScenario();

			DefinitionComponent component = scenario.getFirstAncestorByType(DefinitionComponent.class);

			if (!swimlanes.containsKey(component)) {
				swimlanes.put(component, new HashMap<>());
			}

			if (!swimlanes.get(component).containsKey(scenario)) {
				swimlanes.get(component).put(scenario, new ArrayList<>());
			}

			swimlanes.get(component).get(scenario).add(descriptor);
		}

		getWriter().write("digraph G {\n");
		getWriter().write("\tsize = \"20,20\";\n");
		getWriter().write("\trankdir = \"LR\";\n");

		// Clusters and nodes
		for (Entry<DefinitionComponent, Map<Scenario, List<SimulationDescriptor>>> outerEntry : swimlanes.entrySet()) {
			DefinitionComponent component = outerEntry.getKey();
			
			getWriter().write("\tsubgraph \"cluster:" + Namer.dispatch(component) + "\" {\n");
			getWriter().write("\t\tlabel = \"" + Namer.dispatch(component) + "\";\n");
			
			for (Entry<Scenario, List<SimulationDescriptor>> innerEntry : outerEntry.getValue().entrySet()) {
				Scenario scenario = innerEntry.getKey();
				
				getWriter().write("\t\tsubgraph \"cluster:" + Namer.dispatch(scenario) + "\" {\n");
				getWriter().write("\t\t\tlabel = \"" + Namer.dispatch(scenario) + "\";\n");

				for (SimulationDescriptor descriptor : innerEntry.getValue()) {
					String color = null;
					
					if (descriptor.getResult().equals("Failure")) {
						color = "red";
					} else if (descriptor.getResult().equals("Timeout")) {
						color = "blue";
					} else if (descriptor.getResult().equals("Success")) {
						color = "green";
					} else {
						throw new IllegalStateException("Result not supported: " + descriptor.getResult());
					}
					
					getWriter().write("\t\t\t\"" + descriptor + "\" [label = \"\", shape = circle, style = filled, fillcolor = " + color + ", width = " + descriptor.getCount() / 10.0 + "];\n");
				}
				
				/*
				for (int index = 1; index < innerEntry.getValue().size(); index++) {
					SimulationDescriptor first = innerEntry.getValue().get(index - 1);
					SimulationDescriptor second = innerEntry.getValue().get(index - 0);
					
					getWriter().write("\t\t\"" + first + "\" -> \"" + second + "\"[style = invis];\n");
				}
				*/
				
				getWriter().write("\t\t}\n");
			}
			
			getWriter().write("\t}\n");
		}
		
		// Edges
		for (int index = 1; index < getData().size(); index++) {
			SimulationDescriptor first = getData().get(index - 1);
			SimulationDescriptor second = getData().get(index - 0);
			
			getWriter().write("\t\"" + first + "\" -> \"" + second + "\"[];\n");
		}

		getWriter().write("}");
		getWriter().close();

	}

}
