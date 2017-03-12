package de.tum.imomesa.simulator.evaluators;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.ports.Port;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.materials.MaterialBindingPort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.threads.MaterialBindingPortThread;

public class MaterialBindingPortEvaluator extends PortEvaluator<MaterialBindingPort> {
	
	private List<PortEvaluator<Port>> ports = new ArrayList<>();
	
	private MaterialBindingPortThread thread;
	
	public MaterialBindingPortEvaluator(List<Element> context, MaterialBindingPort port, Memory memory, int step) {
		super(context, port, memory, step);
		
		for (PortInterface nested : element.getPorts()) {
			ports.add(new PortEvaluator<Port>(port.append(context), (Port) nested, memory, step));
		}
	}
	
	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		
		for (PortEvaluator<Port> evaluator : ports) {
			evaluator.prepare();
		}
	}
	
	@Override
	public void initialize() throws InterruptedException {
		super.initialize();
		
		for (PortEvaluator<Port> evaluator : ports) {
			evaluator.initialize();
		}
	}
	
	@Override
	public void createThread() {
		super.createThread();
		
		for (PortEvaluator<Port> evaluator : ports) {
			evaluator.createThread();
		}
		thread = new MaterialBindingPortThread(context, element, memory, step);
	}
	
	@Override
	public void startThread() {
		super.startThread();
		
		for(PortEvaluator<Port> evaluator : ports) {
			evaluator.startThread();
		}
		
		thread.start();
	}
	
	@Override
	public void joinThread() throws InterruptedException {
		super.joinThread();
		
		for (PortEvaluator<Port> evaluator : ports) {
			evaluator.joinThread();
		}
		thread.join();
	}
	
	@Override
	public void cleanup() throws InterruptedException {
		super.cleanup();
		
		for (PortEvaluator<Port> evaluator : ports) {
			evaluator.cleanup();
		}
	}
	
}
