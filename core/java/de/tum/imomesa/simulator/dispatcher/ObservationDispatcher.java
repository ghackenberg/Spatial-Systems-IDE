package de.tum.imomesa.simulator.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Channel;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.DynamicChannel;
import de.tum.imomesa.model.elements.Observation;
import de.tum.imomesa.model.elements.ports.Port;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.PortInterface.Direction;
import de.tum.imomesa.model.elements.ports.PortProxy;
import de.tum.imomesa.model.elements.ports.energies.KinematicEnergyPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialBindingPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialEntryPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialExitPort;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class ObservationDispatcher {
	
	private static ObservationDispatcher instance = new ObservationDispatcher();
	
	public static ObservationDispatcher getInstance() {
		return instance;
	}
	
	private ObservationDispatcher() {
		
	}

	public void dispatch(AbstractThread<?> thread, List<Element> context, Observation observation, Memory memory, int step, Object value) {
		try {
			// Initialize iterator
			Class<?> iterator = observation.getClass();
			// Iterate until you find a right method
			while (Observation.class.isAssignableFrom(iterator)) {
				try {
					// Obtain method
					Method method = getClass().getMethod("set", AbstractThread.class, List.class, iterator, Memory.class, Integer.class, Object.class);
					// Invoke method
					method.invoke(this, thread, context, observation, memory, step, value);
					// Finish set
					return;
				}
				catch (NoSuchMethodException e) {
					// Iterate up
					iterator = iterator.getSuperclass();
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, Observation observation, Memory memory, Integer step, Object value) {
		// Default set operation
		memory.setValue(thread, observation.append(context), step, value);
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, PortInterface port, Memory memory, Integer step, Object value) {
		// Default set operation
		set(thread, context, (Observation) port, memory, step, value);
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, Port port, Memory memory, Integer step, Object value) {
		set(thread, context, port, memory, step, value, true);
	}

	public void set(AbstractThread<?> thread, List<Element> context, Port port, Memory memory, Integer step, Object value, boolean forward) {
		// Obtain proxy
		PortProxy proxy = Utils.getPortProxy(context, port);
		// Determine synchronizer (dynamic channels get appended to the proxy if it exists!)
		PortInterface sync_port = proxy != null ? proxy : port;
		// Synchronize on outgoing channels
		synchronized (sync_port.getOutgoing()) {
			// Propagate the value
			for (Channel channel : Utils.getOutgoingForContext(context, port)) {
				if(channel instanceof DynamicChannel) {
					// Debug
					//System.out.println(thread + ": Following dynamic channel: " + port.append(context) + " -> " + channel.getTarget().append(((DynamicChannel) channel).getTargetContext()));
					// Dispatch
					dispatch(thread, ((DynamicChannel) channel).getTargetContext(), (Observation) channel.getTarget(), memory, step, value);
				}
				else {
					dispatch(thread, channel.getTarget().resolve(context), (Observation) channel.getTarget(), memory, step, value);	
				}
			}
			// Check forward flag
			if (forward) {
				// Only forward output ports
				if(port.checkDirection(Direction.OUTPUT)) {
					// Check proxy
					if (proxy != null) {
						// Debug
						//System.out.println(thread + ": Forwarding proxied output value: " + port.append(context) + " -> " + proxy.append(context.subList(0, context.size() - 1)));
						// Dispatch
						set(thread, context.subList(0, context.size() - 1), proxy, memory, step, value, false);
					}
				}
			}
			// Default set operation
			set(thread, context, (PortInterface) port, memory, step, value);
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, PortProxy proxy, Memory memory, Integer step, Object value) {
		set(thread, context, proxy, memory, step, value, true);
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, PortProxy proxy, Memory memory, Integer step, Object value, boolean forward) {
		// Synchronize on outgoing channels
		synchronized (proxy.getOutgoing()) {
			// Propagate the value
			for (Channel channel : Utils.getOutgoingForContext(context, proxy)) {
				if(channel instanceof DynamicChannel) {
					// Debug
					//System.out.println(thread + ": Following dynamic channel: " + proxy.append(context) + " -> " + channel.getTarget().append(((DynamicChannel) channel).getTargetContext()));
					// Dispatch
					dispatch(thread, ((DynamicChannel) channel).getTargetContext(), (Observation) channel.getTarget(), memory, step, value);
				}
				else {
					dispatch(thread, channel.getTarget().resolve(context), (Observation) channel.getTarget(), memory, step, value);	
				}
			}
			// Check forward flag
			if (forward) {
				// forward value to template
				if(proxy.checkDirection(Direction.INPUT)) {
					// Debug
					//System.out.println(thread + ": Forwarding proxied input value: " + proxy.append(context) + " -> " + proxy.getPortImplementation().append(proxy.getPortImplementation().getParent().append(context)));
					// Dispatch
					set(thread, proxy.getPortImplementation().getParent().append(context), (Observation) proxy.getPortImplementation(), memory, step, value);
				}
			}
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, MaterialBindingPort port, Memory memory, Integer step, Object object) throws InterruptedException
	{
		if (port.getAlwaysActive()) {
			if (step > 0) {
				set(thread, context, (Port) port, memory, step, memory.getCollision(port.append(context), step - 1));
			}
			else {
				set(thread, context, (Port) port, memory, step, new HashSet<List<Element>>());
			}
		}
		else if(object == null) {
			set(thread, context, (Port) port, memory, step, object);
		}
		else if(object instanceof Boolean) {
			if(object.equals(true) && step > 0) {
				set(thread, context, (Port) port, memory, step, memory.getCollision(port.append(context), step - 1));
			}
			else {
				set(thread, context, (Port) port, memory, step, new HashSet<List<Element>>());
			}
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, MaterialEntryPort port, Memory memory, Integer step, Object object) throws InterruptedException
	{
		if (object == null) {
			set(thread, context, (Port) port, memory, step, null);	
		}
		else if (object instanceof Boolean) {
			if ((Boolean) object) {
				
				// Create proxy
				ComponentProxy proxy_component = new ComponentProxy();
				proxy_component.setName("Random" + (int)(Math.random() * 100000 % 1000));
				proxy_component.setTemplate(port.getComponent());
				
				// Create port proxies
				for (PortInterface nested : port.getComponent().getPorts()) {
					// Create port
					PortProxy proxy_port = new PortProxy(nested.getReadType());
					proxy_port.setPortImplementation((Port) nested);
					// Add port
					proxy_component.getPorts().add(proxy_port);
				}
				
				// Set port value
				Set<ComponentProxy> set = new HashSet<>();
				set.add(proxy_component);
				
				set(thread, context, (Port) port, memory, step, set);
			}
			else {
				set(thread, context, (Port) port, memory, step, new HashSet<>());
			}
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, MaterialExitPort port, Memory memory, Integer step, Object object) throws InterruptedException
	{
		if (object == null) {
			set(thread, context, (Port) port, memory, step, null);	
		}
		else if (object instanceof Boolean) {
			if ((Boolean) object == true && step > 0) {
				set(thread, context, (Port) port, memory, step, memory.getCollision(port.append(context), step - 1));
			}
			else {
				set(thread, context, (Port) port, memory, step, new HashSet<>());
			}
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public void set(AbstractThread<?> thread, List<Element> context, KinematicEnergyPort port, Memory memory, Integer step, Object object) throws InterruptedException
	{
		if(object == null) {
			// port is null
			set(thread, context, (Port) port, memory, step, object);
		}
		else if(port.getDirection() == Direction.OUTPUT.ordinal()) {

			// number
			if(object instanceof Number) {
				// multiply transform with number
				if(port.getTransform() != null) {
					double multiplicator = (Double) object;
					
					// calculate transform
					RealMatrix matrix = port.getTransform().multiplyTransform(multiplicator).toRealMatrix();
					RealMatrix parent = new DiagonalMatrix(new double[] {1,1,1,1});
					if (step > 0) {
						parent = memory.getTransform(context, step - 1);
					}
					RealMatrix invers = new LUDecomposition(parent).getSolver().getInverse();
		
					set(thread, context, (Port) port, memory, step, parent.multiply(matrix.multiply(invers)));
				}
				else {
					throw new IllegalStateException("Transform not set.");
				}
			}
			// transform
			else if(object instanceof RealMatrix) {
				set(thread, context, (Port) port, memory, step, object);
			}
			
			// unsupported
			else {
				throw new IllegalStateException();
			}
		}
		else if(port.getDirection() == Direction.INPUT.ordinal()) {
			if(object instanceof RealMatrix) {
				set(thread, context, (Port) port, memory, step, object);
			}
			else {
				MarkerManager.get().addMarker(new ErrorMarker(context, "Input of Input-Kinematic Ports has to be a matrix", step));
			}
		}
		else {
			throw new IllegalStateException("not implemented");
		}
	}

}
