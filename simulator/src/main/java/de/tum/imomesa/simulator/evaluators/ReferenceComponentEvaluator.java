package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.simulator.Memory;

public class ReferenceComponentEvaluator extends ComponentEvaluator<ReferenceComponent>{

	private DefinitionComponentEvaluator template;
	
	public ReferenceComponentEvaluator(List<Element> context, ReferenceComponent element, Memory memory, int step) {
		super(context, element, memory, step);
		
		template = new DefinitionComponentEvaluator(element.append(context), element.getTemplate(), memory, step);
	}

	@Override
	public void prepare() throws InterruptedException {
		super.prepare();
		template.prepare();
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
