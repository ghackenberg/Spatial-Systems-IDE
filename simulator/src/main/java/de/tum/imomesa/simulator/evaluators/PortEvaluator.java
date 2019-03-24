package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.threads.PortThread;

public class PortEvaluator<T extends Port> extends AbstractEvaluator<T> {

	private PortThread thread;

	public PortEvaluator(List<Element> context, T element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	public void prepare() throws InterruptedException {
		memory.initValue(element.append(context), step);
		/*
		// Get potential proxy
		ReferencePort proxy = Utils.getPortProxy(context, element);
		// Check if the port does not have any incoming ports
		// if(element.getIncomingStaticChannels().size() == 0 && (proxy == null
		// || proxy.getIncomingStaticChannels().size() == 0)) {
		if (Utils.getIncomingChannelsForContext(context, element).size() == 0 && (proxy == null
				|| Utils.getIncomingChannelsForContext(context.subList(0, context.size() - 1), proxy).size() == 0)) {
			// Evaluate expression
			Object value = ExpressionDispatcher.getInstance().dispatch(null, context, element.getDefault(), memory,
					step);
			// Set the port value
			ObservationDispatcher.getInstance().dispatch(null, context, element, memory, step, value);
		}
		*/
	}

	@Override
	public void createThread() {
		thread = new PortThread(context, element, memory, step);
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
