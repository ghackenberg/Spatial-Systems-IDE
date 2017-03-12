package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.scenarios.Scenario;
import de.tum.imomesa.model.elements.executables.scenarios.Step;
import de.tum.imomesa.model.elements.executables.scenarios.Transition;
import de.tum.imomesa.simulator.Memory;

public class ScenarioThread extends ExecutableThread<Step, Transition> {

	public ScenarioThread(List<Element> context, Scenario executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}

	@Override
	protected List<Transition> getOutgoingTransitions(Step label) {
		return label.getOutgoing();
	}

	@Override
	protected Step getTargetLabel(Transition transition) {
		return transition.getTargetScenario();
	}

}
