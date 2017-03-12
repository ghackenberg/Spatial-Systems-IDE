package de.tum.imomesa.workbench.controllers.simulation.editors;

import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.workbench.diagrams.DefaultDiagramBehavior;
import de.tum.imomesa.workbench.diagrams.MonitorDiagram;
import javafx.scene.layout.Pane;

public class MonitorController extends ExecutableController<Monitor> {

	@Override
	public void render() {
		Pane pane = new Pane();

		diagram = new MonitorDiagram(pane, element, new DefaultDiagramBehavior<>(), false);

		borderPane.setCenter(pane);
	}

}
