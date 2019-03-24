package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.Channel;
import de.tum.imomesa.model.channels.DynamicChannel;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.model.transforms.RawTransform;
import de.tum.imomesa.simulator.evaluators.ReferenceComponentEvaluator;
import de.tum.imomesa.simulator.events.CreateComponentEvent;
import de.tum.imomesa.simulator.events.DeleteComponentEvent;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.errors.MaterialPortNotSetError;
import de.tum.imomesa.simulator.threads.AbstractThread;
import de.tum.imomesa.simulator.threads.ExecutableThread;
import de.tum.imomesa.simulator.threads.MaterialBindingPortThread;

public class Utils {

	public static Set<MaterialBindingPortThread> getMaterialBindingPortThreads(List<Element> context, Port port,
			Memory memory, int step) throws InterruptedException {

		Set<MaterialBindingPortThread> intermediate = getMaterialBindingPortThreads();
		Set<MaterialBindingPortThread> result = new HashSet<>();

		for (MaterialBindingPortThread thread : intermediate) {
			// Port parent might be colliding component
			Set<List<Element>> collisions = memory.getCollision(thread.getElement().append(thread.getContext()), step);
			for (List<Element> collision : collisions) {
				if (context.equals(collision)) {
					result.add(thread);
					break;
				}
			}
			// Port parent might be interaction material port
			if (thread.getElement().append(thread.getContext()).equals(context)) {
				result.add(thread);
			}
			// Port self might be interaction material port
			if (thread.getElement().append(thread.getContext()).equals(port.append(context))) {
				result.add(thread);
			}
		}

		return result;
	}

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
		return null;
	}

	public static Set<ExecutableThread<?, ?>> getWritingExecutableThreads(List<Element> context, Port port,
			Memory memory, int step) throws InterruptedException {
		Set<ExecutableThread<?, ?>> result = new HashSet<>();

		// Iterate behaviors

		for (ExecutableThread<?, ?> thread : getExecutableThreads()) {
			// Executable
			Executable<?, ?> executable = (Executable<?, ?>) thread.getElement();
			// Check the context!
			if (thread.getContext().equals(context)
					|| thread.getContext().equals(context.subList(0, context.size() - 1))) {
				// Get active label
				Label label = memory.getLabel(executable.append(thread.getContext()), step);
				// Check active label
				if (label != null) {
					// Iterate actions
					for (Action action : label.getActions()) {
						if (action != null && action.getObservation() != null && action.getObservation().equals(port)) {
							result.add(thread);
						}
					}
					// Check scenario
					if (label instanceof Step) {
						// Iterate transitions
						for (Transition<?> transition : executable.getOutgoingTransitions(label)) {
							for (Action action : transition.getActions()) {
								if (action != null && action
										.getObservation() != null /*
																	 * bug in
																	 * database
																	 */ && action.getObservation().equals(port)) {
									result.add(thread);
								}
							}
						}
					}
					// Check behavior
					else if (label instanceof State) {
						// Iterate transitions
						for (Transition<?> transition : executable.getOutgoingTransitions(label)) {
							if (transition != null) {
								for (Action action : transition.getActions()) {
									if (action != null && action.getObservation() != null
											&& action.getObservation().equals(port)) {
										result.add(thread);
									}
								}
							}
						}
					}
					// Monitors are not allowed to set port
				}
			}
		}

		return result;
	}

	public static Set<ExecutableThread<?, ?>> getWritingExecutableThreads(List<Element> context, Variable port,
			Memory memory, int step) throws InterruptedException {
		Set<ExecutableThread<?, ?>> result = new HashSet<>();

		// Iterate behaviors

		for (ExecutableThread<?, ?> thread : getExecutableThreads()) {
			// Executable
			Executable<?, ?> executable = (Executable<?, ?>) thread.getElement();
			// Check the context!
			if (thread.getContext().equals(context)
					|| thread.getContext().equals(context.subList(0, context.size() - 1))) {
				// Get active label
				Label label = memory.getLabel(executable.append(thread.getContext()), step);
				// Iterate actions
				for (Action action : label.getActions()) {
					if (action != null && action.getObservation() != null && action.getObservation().equals(port)) {
						result.add(thread);
					}
				}
				// Check scenario
				if (label instanceof Step) {
					// Iterate transitions
					for (Transition<?> transition : executable.getOutgoingTransitions(label)) {
						for (Action action : transition.getActions()) {
							if (action != null && action.getObservation() != null
									&& action.getObservation().equals(port)) {
								result.add(thread);
							}
						}
					}
				}
				// Check behavior
				else if (label instanceof State) {
					// Iterate transitions
					for (Transition<?> transition : executable.getOutgoingTransitions(label)) {
						for (Action action : transition.getActions()) {
							if (action != null && action.getObservation() != null
									&& action.getObservation().equals(port)) {
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

	public static void createComponent(List<Element> context, EntryLifeMaterialPort port, Memory memory, int step)
			throws InterruptedException {

		Object value = memory.getValue(null, port.append(context), step);
		if (value != null) {
			if (value instanceof Set) {
				Set<?> set = (Set<?>) value;
				if (set.size() == 1) {
					Object intermediate = set.iterator().next();
					if (intermediate instanceof ReferenceComponent) {
						// Create proxy
						ReferenceComponent proxy = (ReferenceComponent) intermediate;
						// Remember proxy
						memory.getAddedProxy(step).add(proxy);
						
						// Initialize transforms
						if (context.get(context.size() - 1) instanceof Scenario) {
							proxy.getTransforms().add(new RawTransform(memory.getTransform(context.subList(0, context.size() - 1), step)));
						} else if (context.get(context.size() - 1) instanceof Component) {
							proxy.getTransforms().add(new RawTransform(memory.getTransform(context, step)));
						} else {
							throw new IllegalStateException();
						}
						proxy.getTransforms().addAll(((EntryLifeMaterialPort) port).getVolume().getTransforms());
						
						// Initialize proxy
						new ReferenceComponentEvaluator(new ArrayList<>(), proxy, memory, step + 1).prepare();
						// Publish event
						EventBus.getInstance().publish(new CreateComponentEvent(context, port, step, proxy));
					} else {
						throw new IllegalStateException("No ComponentProxy to be created");
					}
				} else if (set.size() > 1) {
					throw new IllegalStateException("More than one component created at one step");
				}
			} else {
				MarkerManager.get().addMarker(new MaterialPortNotSetError(port.append(context), step));
			}
		}
	}

	public static void deleteComponent(List<Element> context, ExitLifeMaterialPort port, Memory memory, int step)
			throws InterruptedException {
		Object value = memory.getValue(null, port.append(context), step);
		if (value != null) {
			if (value instanceof Set) {
				Set<?> set = (Set<?>) value;
				for (Object current : set) {
					if (current instanceof List) {
						List<?> list = (List<?>) current;
						if (list.size() > 0) {
							Object first = list.get(0);
							if (first instanceof Element) {
								memory.getRemovedProxy(step).remove(first);
								EventBus.getInstance().publish(new DeleteComponentEvent(context, port, step, (ReferenceComponent) first));
							} else {
								throw new IllegalStateException("Element required!");
							}
						} else {
							throw new IllegalStateException("Size greater zero required!");
						}
					} else {
						throw new IllegalStateException("List required!");
					}
				}
			} else {
				MarkerManager.get().addMarker(new MaterialPortNotSetError(port.append(context), step));
			}
		}
	}

	public static List<Channel> getIncomingChannelsForContext(List<Element> context, Port port) {
		List<Channel> result = new ArrayList<>();

		for (StaticChannel c : port.getIncomingStaticChannels()) {
			if (c.getSource().resolve(context).contains(Context.getInstance().getComponent())) {
				result.add(c);
			}
		}

		for (DynamicChannel c : port.getIncomingDynamicChannels()) {
			if (c.getTargetContext().equals(context)) {
				result.add(c);
			}
		}

		return result;
	}

	public static List<Channel> getOutgoingChannelsForContext(List<Element> context, Port port) {
		List<Channel> result = new ArrayList<>();

		for (StaticChannel c : port.getOutgoingStaticChannels()) {
			if (c.getTarget().resolve(context).contains(Context.getInstance().getComponent())) {
				result.add(c);
			}
		}

		for (DynamicChannel c : port.getOutgoingDynamicChannels()) {
			if (c.getSourceContext().equals(context)) {
				result.add(c);
			}
		}

		return result;
	}

	public static RealMatrix calcTransforms(Component<?> c, List<Element> context, Memory memory, int step)
			throws InterruptedException {

		if (!memory.hasTransform(context, step + 1)) {
			List<Port> kinematicPorts = new ArrayList<>();
			List<List<Element>> contextForPorts = new ArrayList<>();

			// fill lists with kinematic ports and context
			calcAffectingEnergyPorts(context, memory, step, kinematicPorts, contextForPorts);

			RealMatrix m = memory.getTransform(context, step);

			// get all transforms for the last steps
			for (int i = 0; i < kinematicPorts.size(); i++) {
				Port p = kinematicPorts.get(i);
				List<Element> newContext = contextForPorts.get(i);

				if (memory.hasValue(p.append(newContext), step)) {
					Object o = memory.getValue(null, p.append(newContext), step);
					if (o != null) {
						m = ((RealMatrix) o).multiply(m);
					}
				}
			}

			memory.setTransform(context, step + 1, m);
		}

		return memory.getTransform(context, step + 1);
	}

	private static void calcAffectingEnergyPorts(List<Element> context, Memory memory, int step,
			List<Port> kinematicPorts, List<List<Element>> contextForPorts) throws InterruptedException {
		for (int i = 0; i < context.size(); i++) {
			List<Element> newContext = context.subList(0, i + 1);
			Element lastContextData = newContext.get(newContext.size() - 1);
			// check on instance
			if (lastContextData instanceof DefinitionComponent) {
				DefinitionComponent lastContextComp = ((DefinitionComponent) lastContextData);
				// get all input ports of type KinematicEnergyPort
				for (Port p : lastContextComp.getPorts()) {
					if (p instanceof KinematicEnergyPort && p.getDirection() == Direction.INPUT.ordinal()) {
						kinematicPorts.add(0, (KinematicEnergyPort) p);
						contextForPorts.add(0, newContext);
					}
				}
				if (step > 0) {
					for (List<Element> binding : memory.getBinding(newContext, step)) {
						calcAffectingEnergyPorts(binding, memory, step, kinematicPorts, contextForPorts);
					}
				}
			}
		}
	}

	public static ReferencePort getPortProxy(List<Element> context, Port port) {
		if (context.size() > 1 && context.get(context.size() - 2) instanceof ReferenceComponent) {
			ReferenceComponent proxy_component = (ReferenceComponent) context.get(context.size() - 2);
			for (Port proxy_port : proxy_component.getPorts()) {
				if (proxy_port instanceof ReferencePort) {
					if (((ReferencePort) proxy_port).getPortImplementation().equals(port)) {
						return (ReferencePort) proxy_port;
					}
				} else {
					throw new IllegalStateException();
				}
			}
		}
		return null;
	}

}
