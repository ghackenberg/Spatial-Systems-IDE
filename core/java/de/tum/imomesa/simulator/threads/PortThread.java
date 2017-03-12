package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.ports.Port;
import de.tum.imomesa.model.elements.ports.PortProxy;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.ThreadManager;

public class PortThread extends AbstractThread<Port> {

	public PortThread(List<Element> context, Port element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	protected void execute() {
		try {
			// Get proxy
			PortProxy proxy = Utils.getPortProxy(context, element);
			// Join material binding port threads
			ThreadManager.getInstance().joinThreads(this, Utils.getMaterialBindingPortThreads());
			// Check validity
			if (Utils.getMaterialBindingPortThreads().size() > 0) {
				throw new IllegalStateException();
			}
			// Join writing threads
			ThreadManager.getInstance().joinThreads(this, Utils.getWritingExecutableThreads(context, element, memory, step));
			// Check proxy
			if (proxy != null) {
				// Join writing threads
				ThreadManager.getInstance().joinThreads(this, Utils.getWritingExecutableThreads(context.subList(0, context.size() - 1), proxy, memory, step));
			}
			// Check channel
			if (Utils.getIncomingForContext(context, element).size() == 0 && (proxy == null || Utils.getIncomingForContext(context.subList(0, context.size() - 1), proxy).size() == 0)) {
				// Check value
				if (!memory.hasValue(element.append(context), step)) {
					// Set port to null
					ObservationDispatcher.getInstance().dispatch(this, context, element, memory, step, null);
				}
			}
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
