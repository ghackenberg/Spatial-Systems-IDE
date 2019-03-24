package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.threads.MonitorThread;

public class MonitorEvaluator extends ExecutableEvaluator<Activity, Transition, Monitor> {
	
	private MonitorThread thread;

	public MonitorEvaluator(List<Element> context, Monitor executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		thread = new MonitorThread(context, element, memory, step);
	}
	
	@Override
	public void startThread() {
		super.startThread();
		
		thread.start();
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		super.joinThread();
		
		thread.join();
	}

	@Override
	public void cleanup() throws InterruptedException {
		// do nothing!
	}

}
