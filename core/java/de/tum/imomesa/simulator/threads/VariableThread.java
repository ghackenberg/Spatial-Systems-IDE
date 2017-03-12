package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.Variable;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.ThreadManager;

public class VariableThread extends AbstractThread<Variable> {

	public VariableThread(List<Element> context, Variable element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	protected void execute() {
		try {
			// Join writing threads
			ThreadManager.getInstance().joinThreads(this, Utils.getWritingExecutableThreads(context, element, memory, step));
			// Check value
			if (!memory.hasValue(element.append(context), step)) {
				// Set variable to previous value
				ObservationDispatcher.getInstance().dispatch(this, context, element, memory, step, memory.getValue(this, element.append(context),  step - 1));
			}
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
