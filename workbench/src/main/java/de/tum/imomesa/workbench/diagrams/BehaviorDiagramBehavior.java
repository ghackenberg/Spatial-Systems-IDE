package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.behaviors.Transition;

public class BehaviorDiagramBehavior extends ExecutableDiagramBehavior<State, Transition, Behavior> {

	@Override
	protected Transition createTransition() {
		return new Transition();
	}

}
