package de.tum.imomesa.analyzer.serializations.prototypes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.descriptors.SimulationDescriptor;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;

public class HtmlTableSerialization extends Serialization<List<SimulationDescriptor>> {

	private List<DefinitionComponent> outerOrder;
	private List<Scenario> innerOrder;

	public HtmlTableSerialization(List<SimulationDescriptor> data, List<DefinitionComponent> outerOrder,
			List<Scenario> innerOrder, Writer writer) {
		super(data, writer);

		this.outerOrder = outerOrder;
		this.innerOrder = innerOrder;
	}

	@Override
	public void generateResult() throws IOException {
		Set<String> messages = new HashSet<>();
		
		double maxCount = 0;

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

			maxCount = Math.max(maxCount, descriptor.getCount());
		}

		// Html header

		getWriter().write("<html>\n");
		getWriter().write("\t<head>\n");
		getWriter().write("\t\t<link rel=\"stylesheet\" href=\"test.css\"/>\n");
		getWriter().write("\t</head>\n");
		getWriter().write("\t<body>\n");
		getWriter().write("\t\t<table>\n");

		// Table header

		getWriter().write("\t\t\t<thead>\n");
		getWriter().write("\t\t\t<tr>\n");
		getWriter().write("\t\t\t\t<th>Component</th>\n");
		getWriter().write("\t\t\t\t<th>Scenario</th>\n");
		for (int index = 0; index < getData().size(); index++) {
			// getWriter().write("\t\t\t\t<th>" + (index + 1) + "</th>\n");
			getWriter().write("\t\t\t\t<th></th>\n");
		}
		getWriter().write("\t\t\t</tr>\n");
		getWriter().write("\t\t\t</thead>\n");

		// Table body
		int flip = 1;

		getWriter().write("\t\t\t<tbody>\n");
		for (DefinitionComponent outerKey : outerOrder) {
			flip = (flip + 1) % 2;

			Map<Scenario, List<SimulationDescriptor>> values = swimlanes.get(outerKey);

			getWriter().write("\t\t\t<tr class=\"Flip_" + flip + "\">\n");
			getWriter().write("\t\t\t\t<th rowspan=\"" + values.size() + "\">" + Namer.dispatch(outerKey) + "</th>\n");

			boolean first = true;

			for (Scenario innerKey : innerOrder) {
				if (values.containsKey(innerKey)) {
					if (!first) {
						getWriter().write("\t\t\t<tr class=\"Flip_" + flip + "\">\n");
					}

					first = false;

					getWriter().write("\t\t\t\t<th>" + Namer.dispatch(innerKey) + "</th>\n");

					for (SimulationDescriptor descriptor : getData()) {
						for (String message : descriptor.getResult().split(" ")) {
							if (!message.trim().equals("")) {
								messages.add(message.trim());
							}
						}
						
						if (values.get(innerKey).contains(descriptor)) {
							getWriter().write("\t\t\t\t<td class=\"" + descriptor.getResult() + "\"></td>\n");
						} else {
							getWriter().write("\t\t\t\t<td></td>\n");
						}
					}

					getWriter().write("\t\t\t</tr>\n");
				}
			}
		}
		getWriter().write("\t\t\t</tbody>\n");

		// Html footer
		getWriter().write("\t\t\t<tfoot>\n");
		getWriter().write("\t\t\t<tr>\n");
		getWriter().write("\t\t\t\t<td colspan=\"" + (2 + getData().size()) + "\">\n");
		for (String message : messages) {
			getWriter().write("\t\t\t\t\t<div>\n");
			getWriter().write("\t\t\t\t\t\t<div class=\"" + message + "\">&nbsp;</div>\n");
			getWriter().write("\t\t\t\t\t\t<div>" + message.replace('_', ' ') + "</div>\n");
			getWriter().write("\t\t\t\t\t</div>\n");
		}
		getWriter().write("\t\t\t\t</td>\n");
		getWriter().write("\t\t\t</tr>\n");
		getWriter().write("\t\t\t</tfoot>\n");

		getWriter().write("\t\t</table>\n");
		getWriter().write("\t</body>\n");
		getWriter().write("</html>\n");
		getWriter().close();

	}

}
