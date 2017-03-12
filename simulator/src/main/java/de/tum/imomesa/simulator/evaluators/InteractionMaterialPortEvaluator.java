package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.threads.MaterialBindingPortThread;

public class InteractionMaterialPortEvaluator extends PortEvaluator<InteractionMaterialPort> {
	
	private List<PortEvaluator<DefinitionPort>> ports = new ArrayList<>();
	
	private MaterialBindingPortThread thread;
	
	public InteractionMaterialPortEvaluator(List<Element> context, InteractionMaterialPort port, Memory memory, int step) {
		super(context, port, memory, step);
		
		for (DefinitionPort nested : element.getPorts()) {
			ports.add(new PortEvaluator<DefinitionPort>(port.append(context), nested, memory, step));
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		
		for (PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.prepare();
		}
	}
	
	@Override
	public void initialize() throws InterruptedException {
		super.initialize();
		
		for (PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.initialize();
		}
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		for (PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.createThread();
		}
		thread = new MaterialBindingPortThread(context, element, memory, step);
	}
	
	@Override
	public void startThread() {
		super.startThread();
		
		for(PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.startThread();
		}
		
		thread.start();
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		super.joinThread();
		
		for (PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.joinThread();
		}
		thread.join();
	}
	
	@Override
	public void cleanup() throws InterruptedException {
		super.cleanup();
		
		for (PortEvaluator<DefinitionPort> evaluator : ports) {
			evaluator.cleanup();
		}
	}
	
}
