package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.behaviors.Transition;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.threads.BehaviorThread;

public class BehaviorEvaluator extends ExecutableEvaluator<State, Transition, Behavior> {
	
	private BehaviorThread thread;

	public BehaviorEvaluator(List<Element> context, Behavior executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}
	
	@Override
	public void initialize() throws InterruptedException {
		// call super method for variables and properties in labels
		super.initialize();
		
		memory.initLabel(element.append(context), step);
		memory.initTransition(element.append(context), step);
		
		memory.setLabel(element.append(context), step, element.getInitialLabel());
		memory.setTransition(element.append(context), step, null);
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		thread = new BehaviorThread(context, element, memory, step);
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
		// do nothing
	}

}
