package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.simulator.Memory;

public class ComponentProxyEvaluator extends ComponentInterfaceEvaluator<ComponentProxy>{

	private ComponentEvaluator template;
	
	public ComponentProxyEvaluator(List<Element> context, ComponentProxy element, Memory memory, int step) {
		super(context, element, memory, step);
		
		template = new ComponentEvaluator(element.append(context), element.getTemplate(), memory, step);
	}

	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		template.prepare();
	}

	@Override
	public void initialize() throws InterruptedException {
		super.initialize();
		template.initialize();
	}

	@Override
	public void createThread() {
		template.createThread();
	}

	@Override
	public void startThread() {
		template.startThread();
	}

	@Override
	public void joinThread() throws InterruptedException {
		template.joinThread();
	}

	@Override
	public void cleanup() throws InterruptedException {
		template.cleanup();
	}

}
