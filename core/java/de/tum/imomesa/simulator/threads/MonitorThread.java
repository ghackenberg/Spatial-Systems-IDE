package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.monitors.Activity;
import de.tum.imomesa.model.elements.executables.monitors.Monitor;
import de.tum.imomesa.model.elements.executables.monitors.Transition;
import de.tum.imomesa.simulator.Memory;

public class MonitorThread extends ExecutableThread<Activity, Transition> {

	public MonitorThread(List<Element> context, Monitor executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}

	@Override
	protected List<Transition> getOutgoingTransitions(Activity label) {
		return label.getOutgoing();
	}

	@Override
	protected Activity getTargetLabel(Transition transition) {
		return transition.getTargetMonitor();
	}

}
