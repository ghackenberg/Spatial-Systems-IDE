package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.executables.scenarios.Transition;

public class ScenarioDiagramBehavior extends ExecutableDiagramBehavior<Step, Transition, Scenario> {

	@Override
	protected Transition createTransition() {
		return new Transition();
	}

}
