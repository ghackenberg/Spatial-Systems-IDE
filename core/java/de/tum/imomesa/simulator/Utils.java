package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Channel;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.ComponentInterface;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.DynamicChannel;
import de.tum.imomesa.model.elements.executables.Action;
import de.tum.imomesa.model.elements.executables.Executable;
import de.tum.imomesa.model.elements.executables.Label;
import de.tum.imomesa.model.elements.executables.Variable;
import de.tum.imomesa.model.elements.executables.behaviors.State;
import de.tum.imomesa.model.elements.executables.scenarios.Scenario;
import de.tum.imomesa.model.elements.executables.scenarios.Step;
import de.tum.imomesa.model.elements.ports.PortInterface;
import de.tum.imomesa.model.elements.ports.PortInterface.Direction;
import de.tum.imomesa.model.elements.ports.PortProxy;
import de.tum.imomesa.model.elements.ports.energies.KinematicEnergyPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialEntryPort;
import de.tum.imomesa.model.elements.ports.materials.MaterialExitPort;
import de.tum.imomesa.model.elements.transforms.RawTransform;
import de.tum.imomesa.simulator.evaluators.ComponentProxyEvaluator;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.threads.AbstractThread;
import de.tum.imomesa.simulator.threads.ExecutableThread;
import de.tum.imomesa.simulator.threads.MaterialBindingPortThread;

public class Utils {

	public static Set<MaterialBindingPortThread> getMaterialBindingPortThreads() {
		Set<MaterialBindingPortThread> result = new HashSet<>();
		
		for (AbstractThread<?> thread : ThreadManager.getInstance().getThreads()) {
			if (thread instanceof MaterialBindingPortThread) {
				result.add((MaterialBindingPortThread) thread);
			}
		}
		
		return result;
	}

	public static Set<ExecutableThread<?, ?>> getExecutableThreads() {
		Set<ExecutableThread<?, ?>> result = new HashSet<>();
		
		for (AbstractThread<?> thread : ThreadManager.getInstance().getThreads()) {
			if (thread instanceof ExecutableThread<?, ?>) {
				result.add((ExecutableThread<?, ?>) thread);
			}
		}
		
		return result;
	}
	
	public static ExecutableThread<?, ?> getExecutableThread(List<Element> context, Executable<?, ?> executable) {
		for (ExecutableThread<?, ?> thread : getExecutableThreads()) {
			if (thread.getContext().equals(context) && thread.getElement().equals(executable)) {
				return thread;
			}
		}
		throw new IllegalStateException();
	}
	
	public static Set<ExecutableThread<?, ?>> getWritingExecutableThreads(List<Element> context, PortInterface port, Memory memory, int step) throws InterruptedException {
		Set<ExecutableThread<?, ?>> result = new HashSet<>();
		
		// Iterate behaviors
		
		for (ExecutableThread<?, ?> thread : getExecutableThreads()) {
			// Executable
			Executable<?, ?> executable = (Executable<?, ?>) thread.getElement();
			// Check the context!
			if (thread.getContext().equals(context) || thread.getContext().equals(context.subList(0, context.size() - 1))) {
				// Get active label
				Label label = memory.getLabel(executable.append(thread.getContext()), step - 1);
				// Iterate actions
				for (Action action : label.getActions()) {
					if (action.getObservation().equals(port)) {
						result.add(thread);
					}
				}
				// Check scenario
				if (label instanceof Step) {
					// Iterate transitions
					for (de.tum.imomesa.model.elements.executables.scenarios.Transition transition : ((Step) label).getOutgoing()) {
						for (Action action : transition.getActions()) {
							if (action.getObservation().equals(port)) {
								result.add(thread);
							}
						}
					}
				}
				// Check behavior
				else if (label instanceof State) {
					// Iterate transitions
					for (de.tum.imomesa.model.elements.executables.behaviors.Transition transition : ((State) label).getOutgoing()) {
						for (Action action : transition.getActions()) {
							if (action.getObservation().equals(port)) {
								result.add(thread);
							}
						}
					}
				}
				// Monitors are not allowed to set port
			}
		}
		
		return result;
	}
	
	public static Set<ExecutableThread<?, ?>> getWritingExecutableThreads(List<Element> context, Variable port, Memory memory, int step) throws InterruptedException {
		Set<ExecutableThread<?, ?>> result = new HashSet<>();
		
		// Iterate behaviors
		
		for (ExecutableThread<?, ?> thread : getExecutableThreads()) {
			// Executable
			Executable<?, ?> executable = (Executable<?, ?>) thread.getElement();
			// Check the context!
			if (thread.getContext().equals(context) || thread.getContext().equals(context.subList(0, context.size() - 1))) {
				// Get active label
				Label label = memory.getLabel(executable.append(thread.getContext()), step - 1);
				// Iterate actions
				for (Action action : label.getActions()) {
					if (action.getObservation().equals(port)) {
						result.add(thread);
					}
				}
				// Check scenario
				if (label instanceof Step) {
					// Iterate transitions
					for (de.tum.imomesa.model.elements.executables.scenarios.Transition transition : ((Step) label).getOutgoing()) {
						for (Action action : transition.getActions()) {
							if (action.getObservation().equals(port)) {
								result.add(thread);
							}
						}
					}
				}
				// Check behavior
				else if (label instanceof State) {
					// Iterate transitions
					for (de.tum.imomesa.model.elements.executables.behaviors.Transition transition : ((State) label).getOutgoing()) {
						for (Action action : transition.getActions()) {
							if (action.getObservation().equals(port)) {
								result.add(thread);
							}
						}
					}
				}
				// Monitors are not allowed to set port
			}
		}
		
		return result;
	}

	public static void createComponent(List<Element> context, MaterialEntryPort p, Memory memory, int step) throws InterruptedException {
		
		Object value = memory.getValue(null, p.append(context), step);
		if (value != null) {
			if (value instanceof Set) {
				Set<?> set = (Set<?>) value;
				if(set.size() == 1) {
					Object intermediate = set.iterator().next();
					if (intermediate instanceof ComponentProxy) {
						// Create proxy
						ComponentProxy proxy = (ComponentProxy) intermediate;
						// Remember proxy
						memory.getProxy(step).add(proxy);
						// Initialize transforms
						if(context.get(context.size() - 1) instanceof Scenario) {
							proxy.getTransforms().add(new RawTransform(memory.getTransform(context.subList(0, context.size() - 1), step)));
						}
						else if (context.get(context.size() - 1) instanceof ComponentInterface) {
							proxy.getTransforms().add(new RawTransform(memory.getTransform(context, step)));
						}
						else {
							throw new IllegalStateException();
						}
						proxy.getTransforms().addAll(((MaterialEntryPort)p).getVolume().getTransforms());
						// Initialize proxy
						ComponentProxyEvaluator evaluator = new ComponentProxyEvaluator(new ArrayList<>(), proxy, memory, step);
						evaluator.prepare();
						evaluator.initialize();
					}
					else {
						throw new IllegalStateException("No ComponentProxy to be created");
					}
				}
				else if(set.size() > 1) {
					throw new IllegalStateException("More than one component created at one step");
				}
			}
			else {
				MarkerManager.get().addMarker(new ErrorMarker(p.append(context), "Material entry port requires set value!", step));
			}
		}
	}
	
	public static void deleteComponent(List<Element> context, MaterialExitPort p, Memory memory, int step) throws InterruptedException {
		Object value = memory.getValue(null, p.append(context), step);
		if (value != null) {
			if (value instanceof Set) {
				Set<?> set = (Set<?>) value;
				for(Object current : set) {
					if (current instanceof List) {
						List<?> list = (List<?>) current;
						if (list.size() > 0) {
							Object first = list.get(0);
							if (first instanceof Element) {
								memory.getProxy(step).remove(first);
							}
							else {
								throw new IllegalStateException("Element required!");
							}
						}
						else {
							throw new IllegalStateException("Size greater zero required!");
						}
					}
					else {
						throw new IllegalStateException("List required!");
					}
				}
			}
			else {
				MarkerManager.get().addMarker(new ErrorMarker(p.append(context), "Material exit port requires set value!", step));
			}
		}
	}
	
	public static List<Channel> getIncomingForContext(List<Element> context, PortInterface port) {
		List<Channel> result = new ArrayList<>();
		
		for(Channel c : port.getIncoming()) {
			if(c instanceof DynamicChannel) {
				if(((DynamicChannel)c).getTargetContext().equals(context)) {
					result.add(c);
				}
			}
			else {
				result.add(c);
			}
		}
		
		return result;
	}
	
	public static List<Channel> getOutgoingForContext(List<Element> context, PortInterface port) {
		List<Channel> result = new ArrayList<>();
		
		for(Channel c : port.getOutgoing()) {
			if(c instanceof DynamicChannel) {
				if(((DynamicChannel)c).getSourceContext().equals(context)) {
					result.add(c);
				}
			}
			else {
				result.add(c);
			}
		}
		
		return result;
	}
	
	public static RealMatrix calcTransforms(ComponentInterface c, List<Element> context, Memory memory, int step) throws InterruptedException {
		
		if (!memory.hasTransform(context, step)) {
			List<PortInterface> kinematicPorts = new ArrayList<>();
			List<List<Element>> contextForPorts = new ArrayList<>();
			
			// fill lists with kinematic ports and context
			calcAffectingEnergyPorts(context, memory, step, kinematicPorts, contextForPorts);
			
			RealMatrix m = memory.getTransform(context, step - 1);
			
			// get all transforms for the last steps
			for(int i = 0; i < kinematicPorts.size(); i++) {
				PortInterface p = kinematicPorts.get(i);
				List<Element> newContext = contextForPorts.get(i);
				
				if(memory.hasValue(p.append(newContext), step)) {
					Object o = memory.getValue(null, p.append(newContext), step);
					if(o != null) {
						m = ((RealMatrix)o).multiply(m);
					}
				}
			}
			
			memory.setTransform(context, step, m);	
		}
		
		return memory.getTransform(context, step);
	}
	
	// TODO Methode verifizieren!!!
	private static void calcAffectingEnergyPorts(List<Element> context, Memory memory, int step, List<PortInterface> kinematicPorts, List<List<Element>> contextForPorts) throws InterruptedException {
		for(int i = 0; i < context.size(); i++) {
			List<Element> newContext = context.subList(0, i + 1);
			Element lastContextData = newContext.get(newContext.size() - 1);
			// check on instance
			if(lastContextData instanceof Component) {
				Component lastContextComp = ((Component)lastContextData);
				// get all input ports of type KinematicEnergyPort
				for(PortInterface p : lastContextComp.getPorts()) {
					if(p instanceof KinematicEnergyPort && p.getDirection() == Direction.INPUT.ordinal()) {
						kinematicPorts.add(0, (KinematicEnergyPort) p);
						contextForPorts.add(0, newContext);
					}
				}
				if(step > 0) {
					for (List<Element> binding : memory.getBinding(newContext, step)) {
						calcAffectingEnergyPorts(binding, memory, step, kinematicPorts, contextForPorts);
					}
				}
			}
		}
	}
	
	public static PortProxy getPortProxy(List<Element> context, PortInterface port) {
		if (context.size() > 1 && context.get(context.size() - 2) instanceof ComponentProxy) {
			ComponentProxy proxy_component = (ComponentProxy) context.get(context.size() - 2);
			for (PortInterface proxy_port : proxy_component.getPorts()) {
				if (proxy_port instanceof PortProxy) {
					if (((PortProxy) proxy_port).getPortImplementation().equals(port)) {
						return (PortProxy) proxy_port;
					}
				}
				else {
					throw new IllegalStateException();
				}
			}
		}
		return null;
	}
	
}
