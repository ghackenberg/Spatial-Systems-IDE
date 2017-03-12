package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.dispatchers.ExpressionDispatcher;
import de.tum.imomesa.simulator.dispatchers.ObservationDispatcher;
import de.tum.imomesa.simulator.threads.VariableThread;

public class VariableEvaluator extends AbstractEvaluator<Variable> {
	
	private VariableThread thread;

	public VariableEvaluator(List<Element> context, Variable element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	public void prepare() throws InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override
	public void initialize() throws InterruptedException {
		// initialize data structure
		memory.initValue(element.append(context), step);
		// evaluate expression
		Object value = ExpressionDispatcher.getInstance().dispatch(null, element.append(context), element.getDefault(), memory, step);
		// remember value
		ObservationDispatcher.getInstance().dispatch(null, context, element, memory, step, value);
	}

	@Override
	public void createThread() {
		thread = new VariableThread(context, element, memory, step);
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
