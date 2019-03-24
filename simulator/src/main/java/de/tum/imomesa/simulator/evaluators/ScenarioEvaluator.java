package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.executables.scenarios.Transition;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.threads.ScenarioThread;

public class ScenarioEvaluator extends ExecutableEvaluator<Step, Transition, Scenario> {
	
	private List<PortEvaluator<LifeMaterialPort>> ports = new ArrayList<>();
	
	private ScenarioThread thread;

	public ScenarioEvaluator(List<Element> context, Scenario executable, Memory memory, int step) {
		super(context, executable, memory, step);
		
		for (LifeMaterialPort port : executable.getPorts()) {
			ports.add(new PortEvaluator<LifeMaterialPort>(executable.append(context), port, memory, step));
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		
		for (PortEvaluator<LifeMaterialPort> port : ports) {
			port.prepare();
		}
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		for (PortEvaluator<LifeMaterialPort> port : ports) {
			port.createThread();
		}
		
		thread = new ScenarioThread(context, element, memory, step);
	}
	
	@Override
	public void startThread() {
		super.startThread();
		
		for (PortEvaluator<LifeMaterialPort> port : ports) {
			port.startThread();
		}
		
		thread.start();
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		super.joinThread();
		
		for (PortEvaluator<LifeMaterialPort> port : ports) {
			port.joinThread();
		}
		
		// Join thread
		thread.join();
	}
	
	@Override
	public void cleanup() throws InterruptedException {
		for (PortEvaluator<LifeMaterialPort> port : ports) {
			port.cleanup();
		}

		// get new materials from material port in scenario
		for(LifeMaterialPort p : element.getPorts()) {
			if(p instanceof EntryLifeMaterialPort) {
				Utils.createComponent(element.append(context), (EntryLifeMaterialPort) p, memory, step);
			}
			else if(p instanceof ExitLifeMaterialPort) {
				Utils.deleteComponent(element.append(context), (ExitLifeMaterialPort) p, memory, step);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

}
