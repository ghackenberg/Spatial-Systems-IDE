package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.Memory;

public abstract class AbstractEvaluator<T extends Element> {
	
	protected List<Element> context;
	protected T element;
	protected Memory memory;
	protected int step;
	
	public AbstractEvaluator(List<Element> context, T element, Memory memory, int step) {
		this.context = context;
		this.element = element;
		this.memory = memory;
		this.step = step;
	}
	
	public List<Element> getContext() {
		return context;
	}
	
	public abstract void prepare() throws InterruptedException;
	public abstract void initialize() throws InterruptedException;
	public abstract void createThread();
	public abstract void startThread();
	public abstract void joinThread() throws InterruptedException;
	public abstract void cleanup() throws InterruptedException;
	
}
