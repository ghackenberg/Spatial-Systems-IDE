package de.tum.imomesa.workbench.controllers.simulation.editors;

import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.workbench.diagrams.DefaultDiagramBehavior;
import de.tum.imomesa.workbench.diagrams.ScenarioDiagram;
import javafx.scene.layout.Pane;

public class ScenarioController extends ExecutableController<Scenario> {

	@Override
	public void render() {
		Pane pane = new Pane();

		diagram = new ScenarioDiagram(pane, element, new DefaultDiagramBehavior<>(), false);

		borderPane.setCenter(pane);
	}

}