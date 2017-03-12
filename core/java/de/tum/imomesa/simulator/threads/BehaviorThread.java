package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.behaviors.Behavior;
import de.tum.imomesa.model.elements.executables.behaviors.Transition;
import de.tum.imomesa.simulator.Memory;

public class BehaviorThread extends ExecutableThread<de.tum.imomesa.model.elements.executables.behaviors.State, Transition> {

	public BehaviorThread(List<Element> context, Behavior executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}

	@Override
	protected List<Transition> getOutgoingTransitions(de.tum.imomesa.model.elements.executables.behaviors.State label) {
		return label.getOutgoing();
	}

	@Override
	protected de.tum.imomesa.model.elements.executables.behaviors.State getTargetLabel(Transition transition) {
		return transition.getTargetBehavior();
	}

}
