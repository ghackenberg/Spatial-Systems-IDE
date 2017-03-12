package de.tum.imomesa.simulator.threads;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.Transition;
import de.tum.imomesa.simulator.Memory;

public class BehaviorThread extends ExecutableThread<de.tum.imomesa.model.executables.behaviors.State, Transition> {

	public BehaviorThread(List<Element> context, Behavior executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}

	@Override
	protected List<Transition> getOutgoingTransitions(de.tum.imomesa.model.executables.behaviors.State label) {
		return label != null ? getElement().getOutgoingTransitionsTyped(label) : new ArrayList<>();
	}

	@Override
	protected de.tum.imomesa.model.executables.behaviors.State getTargetLabel(Transition transition) {
		return transition.getTargetLabel();
	}
	
	@Override
	public String toDotStyle() {
		return "shape = box, style = filled, fillcolor = red";
	}

}
