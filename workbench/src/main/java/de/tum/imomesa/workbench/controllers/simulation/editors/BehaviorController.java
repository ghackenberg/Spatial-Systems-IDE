package de.tum.imomesa.workbench.controllers.simulation.editors;

import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.workbench.diagrams.BehaviorDiagram;
import de.tum.imomesa.workbench.diagrams.DefaultDiagramBehavior;
import javafx.scene.layout.Pane;

public class BehaviorController extends ExecutableController<Behavior> {

	@Override
	public void render() {
		Pane pane = new Pane();

		diagram = new BehaviorDiagram(pane, element, new DefaultDiagramBehavior<>(), false);

		borderPane.setCenter(pane);
	}

}
