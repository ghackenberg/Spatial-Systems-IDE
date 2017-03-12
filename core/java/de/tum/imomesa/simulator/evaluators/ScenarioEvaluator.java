package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.scenarios.Scenario;
import de.tum.imomesa.model.elements.executables.scenarios.Step;
import de.tum.imomesa.model.elements.executables.scenarios.Transition;
import de.tum.imomesa.model.elements.ports.materials.MaterialEntryPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialExitPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialLifePort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.threads.ScenarioThread;

public class ScenarioEvaluator extends ExecutableEvaluator<Step, Transition, Scenario> {
	
	private List<PortEvaluator<MaterialLifePort>> ports = new ArrayList<>();
	
	private ScenarioThread thread;

	public ScenarioEvaluator(List<Element> context, Scenario executable, Memory memory, int step) {
		super(context, executable, memory, step);
		
		for (MaterialLifePort port : executable.getPorts()) {
			ports.add(new PortEvaluator<MaterialLifePort>(executable.append(context), port, memory, step));
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.prepare();
		}
	}
	
	@Override
	public void initialize() throws InterruptedException {
		super.initialize();
		
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.initialize();
		}
		
		// set step
		memory.setLabel(element.append(context), step, element.getInitialScenario());
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.createThread();
		}
		
		thread = new ScenarioThread(context, element, memory, step);
	}
	
	@Override
	public void startThread() {
		super.startThread();
		
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.startThread();
		}
		
		thread.start();
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		super.joinThread();
		
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.joinThread();
		}
		
		// Join thread
		thread.join();
	}
	
	@Override
	public void cleanup() throws InterruptedException {
		for (PortEvaluator<MaterialLifePort> port : ports) {
			port.cleanup();
		}

		// get new materials from material port in scenario
		for(MaterialLifePort p : element.getPorts()) {
			if(p instanceof MaterialEntryPort) {
				Utils.createComponent(element.append(context), (MaterialEntryPort) p, memory, step);
			}
			else if(p instanceof MaterialExitPort) {
				Utils.deleteComponent(element.append(context), (MaterialExitPort) p, memory, step);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

}
