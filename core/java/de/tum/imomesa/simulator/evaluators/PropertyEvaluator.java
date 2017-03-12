package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.properties.Property;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.dispatcher.ExpressionDispatcher;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.threads.PropertyThread;

public class PropertyEvaluator extends AbstractEvaluator<Property> {
	
	private PropertyThread thread;

	public PropertyEvaluator(List<Element> context, Property element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	public void prepare() throws InterruptedException {
		memory.initValue(element.append(context), step);
	}

	@Override
	public void initialize() throws InterruptedException {
		// Evaluate expression
		Object value = ExpressionDispatcher.getInstance().dispatch(null, element.append(context), element.getDefault(), memory, step);
		// Remember value
		ObservationDispatcher.getInstance().dispatch(null, context, element, memory, step, value);
	}

	@Override
	public void createThread() {
		thread = new PropertyThread(context, element, memory, step);
	}

	@Override
	public void startThread() {
		thread.start();
	}

	@Override
	public void joinThread() throws InterruptedException {
		thread.join();
	}

	@Override
	public void cleanup() throws InterruptedException {
		// do nothing
	}

}
