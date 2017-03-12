package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.managers.ThreadManager;

public abstract class AbstractThread<T extends Element> extends Thread {
	
	protected List<Element> context;
	protected T element;
	protected Memory memory;
	protected int step;
	
	public AbstractThread(List<Element> context, T element, Memory memory, int step) {
		super(element.append(context).toString());
		
		this.context = context;
		this.element = element;
		this.memory = memory;
		this.step = step;
		
		ThreadManager.getInstance().register(this);
	}
	
	@Override
	public final void run() {
		try {
			execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ThreadManager.getInstance().unregister(this);
		}
	}
	
	protected abstract void execute();
	
	public List<Element> getContext() {
		return context;
	}
	
	public T getElement() {
		return element;
	}

}
