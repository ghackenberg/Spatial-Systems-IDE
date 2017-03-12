package de.tum.imomesa.workbench.controllers.simulation.editors.component;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.simulation.AbstractElementController;
import de.tum.imomesa.workbench.diagrams.ComponentDiagram;
import de.tum.imomesa.workbench.diagrams.DefaultDiagramBehavior;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ComponentsController extends AbstractElementController<OverviewElement<Component<?>>> {

	private DefinitionComponent component;
	private ComponentDiagram diagram;

	@Override
	public void render() {
		component = (DefinitionComponent) context.get(context.size() - 1);

		Pane pane = new Pane();

		diagram = new ComponentDiagram(pane, component, new DefaultDiagramBehavior<>(), false);

		borderPane.setCenter(pane);
	}

	@Override
	public void handle(SimulationStepEvent event) {

		for (StaticChannel channel : component.getChannels()) {
			setElementText(channel, channel.getSource(), event.getStep());
		}

		for (DefinitionPort port : component.getPorts()) {
			if (port instanceof InteractionMaterialPort) {
				setElementText(port, port, event.getStep());
				InteractionMaterialPort inter = (InteractionMaterialPort) port;
				for (DefinitionPort nested : inter.getPorts()) {
					setElementText(nested, nested, event.getStep());
				}
			}
			if (!hasStaticChannel(port)) {
				setElementText(port, port, event.getStep());
			}
		}
	}

	private void setElementText(Element element, Port port, int step) {
		try {
			VBox flow = diagram.resolveOnly(element, VBox.class);
			if (flow.getChildren().size() > 1) {
				flow.getChildren().remove(1);
			}

			Memory memory = simulator.getMemory();
			List<Element> myContext = port.resolve(context);

			if (memory.hasValue(port.append(myContext), step)) {
				Object value = memory.getValue(null, port.append(myContext), step);

				Text text = new Text();
				text.setFill(Color.WHITE);
				text.setText("" + value);

				flow.getChildren().add(text);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean hasStaticChannel(Port port) {
		for (StaticChannel channel : component.getChannels()) {
			if (channel.getSource().equals(port)) {
				return true;
			}
			if (channel.getTarget().equals(port)) {
				return true;
			}
		}
		return false;
	}

}