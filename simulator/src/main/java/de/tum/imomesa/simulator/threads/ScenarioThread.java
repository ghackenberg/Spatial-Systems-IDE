package de.tum.imomesa.simulator.threads;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.executables.scenarios.Transition;
import de.tum.imomesa.simulator.Memory;

public class ScenarioThread extends ExecutableThread<Step, Transition> {

	public ScenarioThread(List<Element> context, Scenario executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}

	@Override
	protected List<Transition> getOutgoingTransitions(Step label) {
		return label != null ? getElement().getOutgoingTransitionsTyped(label) : new ArrayList<>();
	}

	@Override
	protected Step getTargetLabel(Transition transition) {
		return transition.getTargetLabel();
	}
	
	@Override
	public String toDotStyle() {
		return "shape = box, style = filled, fillcolor = yellow";
	}

}
