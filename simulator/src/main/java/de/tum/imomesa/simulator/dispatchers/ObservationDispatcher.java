package de.tum.imomesa.simulator.dispatchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.channels.Channel;
import de.tum.imomesa.model.channels.DynamicChannel;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.errors.WrongInputKinematicPortError;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class ObservationDispatcher {
	
	private static ObservationDispatcher instance = new ObservationDispatcher();
	
	public static ObservationDispatcher getInstance() {
		return instance;
	}
	
	private int number;
	
	private ObservationDispatcher() {
		
	}
	
	public void reset() {
		number = 0;
	}
	
	public void dispatch(AbstractThread<?> thread, List<Element> context, Observation observation, Memory memory, int step, Object value) {
		dispatch(thread, context, observation, memory, step, value, true);
	}

	protected void dispatch(AbstractThread<?> thread, List<Element> context, Observation observation, Memory memory, int step, Object value, boolean forward) {
		try {
			// Initialize iterator
			Class<?> iterator = observation.getClass();
			// Iterate until you find a right method
			while (Observation.class.isAssignableFrom(iterator)) {
				try {
					// Obtain method
					Method method = getClass().getDeclaredMethod("set", AbstractThread.class, List.class, iterator, Memory.class, Integer.class, Object.class, Boolean.class);
					// Debug output
					// System.out.println("dispatch(" + thread + ", " + observation.append(context) + " = " + iterator.getName() + ")");
					// Invoke method
					method.invoke(this, thread, context, observation, memory, step, value, forward);
					// Finish set
					return;
				} catch (NoSuchMethodException e) {
					// Iterate up
					iterator = iterator.getSuperclass();
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, Observation observation, Memory memory, Integer step, Object value, Boolean forward) {
		// Default set operation
		memory.setValue(thread, observation.append(context), step, value);
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, Port port, Memory memory, Integer step, Object value, Boolean forward) {
		// Default set operation
		set(thread, context, (Observation) port, memory, step, value, forward);
	}

	protected void set(AbstractThread<?> thread, List<Element> context, DefinitionPort port, Memory memory, Integer step, Object value, Boolean forward) {
		// Debug
		// System.out.println("set(" + thread + ", " + port.append(context) + " = " + value + ")");
		// Obtain proxy
		ReferencePort proxy = Utils.getPortProxy(context, port);
		// Determine synchronizer (dynamic channels get appended to the proxy if it exists!)
		Port sync_port = proxy != null ? proxy : port;
		// Synchronize on outgoing channels
		synchronized (sync_port.getOutgoingDynamicChannels()) {
			// Default set operation
			set(thread, context, (Port) port, memory, step, value, forward);
			// Propagate the value
			for (Channel channel : Utils.getOutgoingChannelsForContext(context, port)) {
				if(channel instanceof DynamicChannel) {
					// Debug
					// System.out.println("forward(" + thread + ", " + port.append(context) + " -> " + channel.getTarget().append(((DynamicChannel) channel).getTargetContext()) + ")");
					// Dispatch
					dispatch(thread, ((DynamicChannel) channel).getTargetContext(), channel.getTarget(), memory, step, value, forward);
				} else {
					// Debug
					// System.out.println("forward(" + thread + ", " + port.append(context) + " -> " + channel.getTarget().append(channel.getTarget().resolve(context)) + ")");
					// Dispatch
					dispatch(thread, channel.getTarget().resolve(context), channel.getTarget(), memory, step, value, forward);	
				}
			}
			// Check proxy
			if (forward && proxy != null) {
				// Obtain proxy context
				List<Element> proxyContext = context.subList(0, context.size() - 1);
				// Debug
				// System.out.println("forward(" + thread + ", " + port.append(context) + " -> " + proxy.append(proxyContext) + ")");
				// Dispatch
				dispatch(thread, proxyContext, proxy, memory, step, value, false);
			}
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, ReferencePort proxy, Memory memory, Integer step, Object value, Boolean forward) {
		// Debug
		// System.out.println("set(" + thread + ", " + proxy.append(context) + " = " + value + ")");
		// Synchronize on outgoing channels
		synchronized (proxy.getOutgoingDynamicChannels()) {
			// Obtain impl context
			List<Element> implContext = proxy.getPortImplementation().getParent().append(context);
			// Check memory value
			if (forward) {
				// Debug
				// System.out.println("forward(" + thread + ", " + proxy.append(context) + " -> " + proxy.getPortImplementation().append(implContext));
				// Dispatch
				dispatch(thread, implContext, proxy.getPortImplementation(), memory, step, value, false);
			}
			// Try updating value
			try {
				// Update value
				value = memory.getValue(thread, proxy.getPortImplementation().append(implContext), step);
			} catch (InterruptedException e) {
				// Report
				throw new IllegalStateException(e);
			}
			// Propagate the value
			for (Channel channel : Utils.getOutgoingChannelsForContext(context, proxy)) {
				if(channel instanceof DynamicChannel) {
					// Debug
					// System.out.println("forward(" + thread + ", " + proxy.append(context) + " -> " + channel.getTarget().append(((DynamicChannel) channel).getTargetContext()) + ")");
					// Dispatch
					dispatch(thread, ((DynamicChannel) channel).getTargetContext(), channel.getTarget(), memory, step, value, forward);
				} else {
					// Debug
					// System.out.println("forward(" + thread + ", " + proxy.append(context) + " -> " + channel.getTarget().append(channel.getTarget().resolve(context)) + ")");
					// Dispatch
					dispatch(thread, channel.getTarget().resolve(context), channel.getTarget(), memory, step, value, forward);	
				}
			}
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, InteractionMaterialPort port, Memory memory, Integer step, Object object, Boolean forward) throws InterruptedException {
		if (port.getAlwaysActive()) {
			set(thread, context, (DefinitionPort) port, memory, step, memory.getCollision(port.append(context), step), forward);
		} else if(object == null) {
			set(thread, context, (DefinitionPort) port, memory, step, object, forward);
		} else if(object instanceof Boolean) {
			if(object.equals(true)) {
				set(thread, context, (DefinitionPort) port, memory, step, memory.getCollision(port.append(context), step), forward);
			} else {
				set(thread, context, (DefinitionPort) port, memory, step, new HashSet<List<Element>>(), forward);
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, EntryLifeMaterialPort port, Memory memory, Integer step, Object object, Boolean forward) throws InterruptedException {
		if (object == null) {
			set(thread, context, (DefinitionPort) port, memory, step, null, forward);	
		} else if (object instanceof Boolean) {
			if ((Boolean) object) {
				
				// Create proxy
				ReferenceComponent proxy_component = new ReferenceComponent();
				proxy_component.setName("Generated component " + (number++));
				proxy_component.setTemplate(port.getComponent());
				
				// Create port proxies
				for (DefinitionPort nested : port.getComponent().getPorts()) {
					// Create port
					ReferencePort proxy_port = new ReferencePort(proxy_component, nested);
					// Add port
					proxy_component.getPorts().add(proxy_port);
				}
				
				// Set port value
				Set<ReferenceComponent> set = new HashSet<>();
				set.add(proxy_component);
				
				set(thread, context, (DefinitionPort) port, memory, step, set, forward);
			} else {
				set(thread, context, (DefinitionPort) port, memory, step, new HashSet<>(), forward);
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, ExitLifeMaterialPort port, Memory memory, Integer step, Object object, Boolean forward) throws InterruptedException {
		if (object == null) {
			set(thread, context, (DefinitionPort) port, memory, step, null, forward);	
		} else if (object instanceof Boolean) {
			if ((Boolean) object) {
				set(thread, context, (DefinitionPort) port, memory, step, memory.getCollision(port.append(context), step), forward);
			} else {
				set(thread, context, (DefinitionPort) port, memory, step, new HashSet<>(), forward);
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	protected void set(AbstractThread<?> thread, List<Element> context, KinematicEnergyPort port, Memory memory, Integer step, Object object, Boolean forward) throws InterruptedException {
		if(object == null) {
			// port is null
			set(thread, context, (DefinitionPort) port, memory, step, object, forward);
		} else if(port.getDirection() == Direction.OUTPUT.ordinal()) {
			if(object instanceof Number) {
				// multiply transform with number
				if(port.getTransform() != null) {
					double multiplicator = (Double) object;
					
					// calculate transform
					RealMatrix matrix = port.getTransform().multiplyTransform(multiplicator).toRealMatrix();
					RealMatrix parent = memory.getTransform(context, step);
					RealMatrix invers = new LUDecomposition(parent).getSolver().getInverse();
		
					set(thread, context, (DefinitionPort) port, memory, step, parent.multiply(matrix.multiply(invers)), forward);
				} else {
					throw new IllegalStateException("Transform not set.");
				}
			} else if(object instanceof RealMatrix) {
				set(thread, context, (DefinitionPort) port, memory, step, object, forward);
			} else {
				throw new IllegalStateException();
			}
		} else if(port.getDirection() == Direction.INPUT.ordinal()) {
			if(object instanceof RealMatrix) {
				set(thread, context, (DefinitionPort) port, memory, step, object, forward);
			} else {
				MarkerManager.get().addMarker(new WrongInputKinematicPortError(context, step));
			}
		} else {
			throw new IllegalStateException("not implemented");
		}
	}

}
