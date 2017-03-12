package de.tum.imomesa.analyzer.serializations.prototypes;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.imomesa.analyzer.descriptors.EventDescriptor;
import de.tum.imomesa.analyzer.helpers.Discretizer;
import de.tum.imomesa.analyzer.helpers.Namer;
import de.tum.imomesa.analyzer.serializations.Serialization;

public class HtmlSwimlaneSerialization<T1> extends Serialization<Map<T1, List<EventDescriptor>>> {

	private List<T1> order;
	private Discretizer discretizer;

	public HtmlSwimlaneSerialization(Map<T1, List<EventDescriptor>> data, List<T1> order, Writer writer,
			Discretizer discretizer) {
		super(data, writer);

		this.order = order;
		this.discretizer = discretizer;
	}

	@Override
	public void generateResult() throws IOException {
		Set<String> messages = new HashSet<>();
		
		double duration = discretizer.getMaximumTimestamp() - discretizer.getMinimumTimestamp();

		getWriter().write("<html>\n");
		getWriter().write("\t<head>\n");
		getWriter().write("\t\t<link rel=\"stylesheet\" href=\"test3.css\"/>\n");
		getWriter().write("\t</head>\n");
		getWriter().write("\t<body>\n");
		getWriter().write("\t\t<table>\n");
		
		// Table head

		getWriter().write("\t\t\t<thead>\n");
		getWriter().write("\t\t\t<tr>\n");
		getWriter().write("\t\t\t\t<th>Component</th>\n");
		getWriter().write("\t\t\t\t<th>Timeline</th>\n");
		getWriter().write("\t\t\t</tr>\n");
		getWriter().write("\t\t\t</thead>\n");
		
		// Table body

		getWriter().write("\t\t\t<tbody>\n");
		for (T1 key : order) {
			getWriter().write("\t\t\t<tr>\n");
			getWriter().write("\t\t\t\t<th>" + Namer.dispatch(key) + "</th>\n");
			getWriter().write("\t\t\t\t<td>\n");
			getWriter().write("\t\t\t\t\t<div class=\"swimlane\">\n");

			double lastPosition = 0;
			String lastMessage = "Empty";
			String lastDescription = "Not created yet.";

			for (EventDescriptor event : getData().get(key)) {
				double currentPosition = (event.getTimestamp() - discretizer.getMinimumTimestamp()) / duration;
				String currentMessage = event.getMessage();
				String currentDescription = event.getDescription();
				
				for (String message : currentMessage.split(" ")) {
					if (!message.trim().equals("")) {
						messages.add(message.trim());
					}
				}

				getWriter().write("\t\t\t\t\t\t<div class=\"interval " + lastMessage + "\" title=\"" + lastDescription
						+ "\" style=\"left: " + lastPosition * 100 + "%; right: " + (1 - currentPosition) * 100
						+ "%;\"></div>\n");
				getWriter().write("\t\t\t\t\t\t<div class=\"point " + currentMessage + "\" title=\""
						+ currentDescription + "\" style=\"left: " + currentPosition * 100 + "%;\"></div>\n");

				lastPosition = currentPosition;
				lastMessage = currentMessage;
				lastDescription = currentDescription;
			}

			getWriter().write("\t\t\t\t\t\t<div class=\"interval " + lastMessage + "\" title=\"" + lastDescription
					+ "\" style=\"left: " + lastPosition * 100 + "%; right: 0%;\"></div>\n");

			getWriter().write("\t\t\t\t\t</div>\n");
			getWriter().write("\t\t\t\t</td>\n");
			getWriter().write("\t\t\t</tr>\n");
		}
		getWriter().write("\t\t\t</tbody>\n");
		
		// Table foot
		
		getWriter().write("\t\t\t<tfoot>\n");
		getWriter().write("\t\t\t<tr>\n");
		getWriter().write("\t\t\t\t<td colspan=\"2\">\n");
		
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
