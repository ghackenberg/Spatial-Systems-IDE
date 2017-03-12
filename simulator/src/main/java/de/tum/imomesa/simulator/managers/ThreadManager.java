package de.tum.imomesa.simulator.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.DynamicChannel;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.threads.AbstractThread;
import de.tum.imomesa.simulator.threads.ExecutableThread;
import de.tum.imomesa.simulator.threads.MaterialBindingPortThread;
import de.tum.imomesa.simulator.threads.PortThread;
import de.tum.imomesa.simulator.threads.PropertyThread;
import de.tum.imomesa.simulator.threads.VariableThread;

public class ThreadManager {
	
	// Static
	
	private static ThreadManager INSTANCE = new ThreadManager();

	public static ThreadManager getInstance() {
		return INSTANCE;
	}
	
	// Dynamic

	private CopyOnWriteArraySet<AbstractThread<?>> threads;
	private ConcurrentHashMap<AbstractThread<?>, AbstractThread<?>> threadToThreads;
	private ConcurrentHashMap<AbstractThread<?>, List<Element>> threadToObservations;
	private boolean running;
	
	private ThreadManager() {
		threads = new CopyOnWriteArraySet<>();
		threadToThreads = new ConcurrentHashMap<>();
		threadToObservations = new ConcurrentHashMap<>();
		running = false;
	}
	
	public synchronized void register(AbstractThread<?> thread) {
		if (!hasThread(thread)) {
			threads.add(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unregister(AbstractThread<?> thread) {
		if (hasThread(thread) && isUnblocked(thread)) {
			threads.remove(thread);
			unblockThread(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void blockObservation(AbstractThread<?> thread, List<Element> context) throws InterruptedException {
		if (!hasBlockedObservation(thread)) {
			threadToObservations.put(thread, context);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void blockThread(AbstractThread<?> thread, AbstractThread<?> otherThread) throws InterruptedException {
		if (!hasBlockedThread(thread)) {
			threadToThreads.put(thread, otherThread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unblockObservation(AbstractThread<?> thread, List<Element> context) {
		if (blocksObservation(thread, context)) {
			threadToObservations.remove(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unblockThread(AbstractThread<?> thread, AbstractThread<?> otherThread) {
		if (blocksThread(thread, otherThread)) {
			threadToThreads.remove(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}

	public synchronized void unblockObservation(List<Element> context) {
		for (AbstractThread<?> thread : threads) {
			if (blocksObservation(thread, context)) {
				unblockObservation(thread, context);
			}
		}
	}

	public synchronized void unblockThread(AbstractThread<?> otherThread) {
		for (AbstractThread<?> thread : threads) {
			if (blocksThread(thread, otherThread)) {
				unblockThread(thread, otherThread);
			}
		}
	}
	
	public synchronized <T extends AbstractThread<?>> void joinThread(AbstractThread<?> thread, T otherThread) throws InterruptedException {
		if (hasThread(otherThread)) {
			blockThread(thread, otherThread);
		}
		while (hasBlockedThread(thread)) {
			wait();
		}
	}
	
	public synchronized <T extends AbstractThread<?>> void joinThreads(AbstractThread<?> thread, Set<T> otherThreads) throws InterruptedException {
		for (AbstractThread<?> otherThread : otherThreads) {
			joinThread(thread, otherThread);
		}
	}
	
	public synchronized void join() throws InterruptedException {
		while (hasUnblocked()) {
			/*
			for (AbstractThread<?> thread : threads) {
				if (isBlocked(thread)) {
					System.out.print("Blocked thread " + thread + ": ");
					if (hasBlockedObservation(thread)) {
						System.out.print(getBlockedObservation(thread));
					}
					if (hasBlockedObservation(thread) && hasBlockedThread(thread)) {
						System.out.print(", ");
					}
					if (hasBlockedThread(thread)) {
						System.out.print(getBlockedThread(thread));
					}
					System.out.println();
				}
				else {
					System.out.println("Unblocked thread " + thread);
				}
			}
			*/
			wait();
			/*
			System.out.println("end");
			*/
		}
		return;
	}
	
	// Non-thread-safe relevant
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public boolean hasBlocked() {
		for (AbstractThread<?> thread : threads) {
			if (isBlocked(thread)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasUnblocked() {
		for (AbstractThread<?> thread : threads) {
			if (isUnblocked(thread)) {
				return true;
			}
		}
		return false;
	}
	public boolean isUnblocked(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return !threadToThreads.containsKey(thread) && !threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean isBlocked(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread) || threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean hasBlockedObservation(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	public boolean hasBlockedThread(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public List<Element> getBlockedObservation(AbstractThread<?> thread) {
		if (hasThread(thread) && hasBlockedObservation(thread)) {
			return threadToObservations.get(thread);
		}
		else {
			return new ArrayList<>();
			//throw new IllegalStateException();
		}
	}
	
	public AbstractThread<?> getBlockedThread(AbstractThread<?> thread) {
		if (hasThread(thread) && hasBlockedThread(thread)) {
			return threadToThreads.get(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean blocksObservation(AbstractThread<?> thread, List<Element> context) {
		if (hasThread(thread)) {
			return threadToObservations.containsKey(thread) && threadToObservations.get(thread).equals(context);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean blocksThread(AbstractThread<?> thread, AbstractThread<?> otherThread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread) && threadToThreads.get(thread).equals(otherThread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean hasThread(AbstractThread<?> thread) {
		return threads.contains(thread);
	}
	
	public Set<List<Element>> getObservations() {
		Set<List<Element>> result = new HashSet<>();
		
		for (AbstractThread<?> thread : threads) {
			if (hasBlockedObservation(thread)) {
				result.add(getBlockedObservation(thread));
			}
		}
		
		return result;
	}
	
	public Set<List<Element>> getObservationsRecursive() {
		Set<List<Element>> result = new HashSet<>();
		
		for (List<Element> observation : getObservations()) {
			if (observation.size() > 0 && observation.get(observation.size() - 1) instanceof Port) {
				getObservationsRecursive(observation.subList(0, observation.size() - 1), (Port) observation.get(observation.size() - 1), result);
			}
			else {
				result.add(observation);
			}
		}
		
		return result;
	}
	
	private void getObservationsRecursive(List<Element> context, Port port, Set<List<Element>> result) {
		if (!result.contains(port.append(context))) {
			result.add(port.append(context));
			
			for (StaticChannel channel : port.getIncomingStaticChannels()) {
				getObservationsRecursive(channel.getSource().resolve(context), channel.getSource(), result);
			}
			for (DynamicChannel channel : port.getIncomingDynamicChannels()) {
				if (channel.getTargetContext().equals(context)) {
					getObservationsRecursive(channel.getSourceContext(), channel.getSource(), result);
				}
			}
			
			if (port instanceof DefinitionPort) {
				ReferencePort port_proxy = Utils.getPortProxy(context, port);
				
				if (port_proxy != null) {
					getObservationsRecursive(context.subList(0, context.size() - 1), port_proxy, result);
				}
			}
			else if (port instanceof ReferencePort) {
				getObservationsRecursive(((ReferencePort) port).getPortImplementation().getParent().append(context), ((ReferencePort) port).getPortImplementation(), result);
			}
		}
	}
	
	public Set<AbstractThread<?>> getThreads() {
		return threads;
	}
	
	public String toDot() {
		// Get observations
		Set<List<Element>> observations = getObservationsRecursive();
		
		// Create result
		String result = "";
		
		result += "digraph {\n";
		
		// Thread nodes
		for (AbstractThread<?> thread : threads) {
			if (thread instanceof PortThread) {
				PortThread port_thread = (PortThread) thread;
				if (observations.contains(port_thread.getElement().append(port_thread.getContext()))) {
					result += "\t\"" + thread + "\" [" + thread.toDotLabel() + ", " + thread.toDotStyle() + "];\n";
				}
			}
			else if (!(thread instanceof PropertyThread)) {
				result += "\t\"" + thread + "\" [" + thread.toDotLabel() + ", " + thread.toDotStyle() + "];\n";
			}
		}
		
		// Observation nodes
		for (List<Element> observation : observations) {
			String label = observation.get(observation.size() - 1).getClass().getSimpleName() + "\\n";
			
			for (Element element : observation) {
				label += "\\n" + element;
			}
			
			result += "\t\"" + observation + "\" [label = \"" + label + "\", shape = ellipse, style = filled, color = pink];\n";
		}
		
		// Edges
		for (AbstractThread<?> thread : threads) {
			if (!(thread instanceof PropertyThread) && (!(thread instanceof PortThread) || observations.contains(((PortThread) thread).getElement().append(((PortThread) thread).getContext())))) {	
				if (hasBlockedThread(thread)) {
					result += "\t\"" + getBlockedThread(thread) + "\" -> \"" + thread + "\" [label = \"blocking\"];\n";
				}
				if (hasBlockedObservation(thread)) {
					result += "\t\"" + getBlockedObservation(thread) + "\" -> \"" + thread + "\" [label = \"blocking\"];\n";
				}
			}
			if (thread instanceof ExecutableThread<?, ?>) {
				for (List<Element> observation : observations) {
					Executable<?, ?> executable = ((ExecutableThread<?, ?>) thread).getElement();
					boolean found = false;
					for (Label label : executable.getLabels()) {
						for (Action action : label.getActions()) {
							found = found || observation.equals(action.getObservation().append(action.getObservation().resolve(thread.getContext())));
						}
					}
					for (Transition<?> transition : executable.getTransitions()) {
						for (Action action : transition.getActions()) {
							found = found || observation.equals(action.getObservation().append(action.getObservation().resolve(thread.getContext())));
						}
					}
					if (found) {
						result += "\t\"" + thread + "\" -> \"" + observation + "\" [label = \"writing\", style = dashed];\n";
					}
				}
			}
			if (thread instanceof MaterialBindingPortThread) {
				MaterialBindingPortThread observation_thread = (MaterialBindingPortThread) thread;
				if (observations.contains(observation_thread.getElement().append(observation_thread.getContext()))) {
					result += "\t\"" + thread + "\" -> \"" + observation_thread.getElement().append(observation_thread.getContext()) + "\" [label = \"writing\", style = dashed];\n";
				}
			}
			if (thread instanceof PortThread) {
				PortThread observation_thread = (PortThread) thread;
				if (observations.contains(observation_thread.getElement().append(observation_thread.getContext()))) {
					result += "\t\"" + thread + "\" -> \"" + observation_thread.getElement().append(observation_thread.getContext()) + "\" [label = \"writing\", style = dashed];\n";
				}
			}
			if (thread instanceof VariableThread) {
				VariableThread observation_thread = (VariableThread) thread;
				if (observations.contains(observation_thread.getElement().append(observation_thread.getContext()))) {
					result += "\t\"" + thread + "\" -> \"" + observation_thread.getElement().append(observation_thread.getContext()) + "\" [label = \"writing\", style = dashed];\n";
				}
			}
		}
		for (List<Element> observation : observations) {
			if (observation.get(observation.size() - 1) instanceof Port) {
				
				List<Element> context = observation.subList(0, observation.size() - 1);
				Port port = (Port) observation.get(observation.size() - 1);
				
				for (StaticChannel channel : port.getIncomingStaticChannels()) {
					result += "\t\"" + channel.getSource().append(channel.getSource().resolve(context)) + "\" -> \"" + port.append(context) + "\" [label = \"static channel\", color = green]; \n";
				}
				for (DynamicChannel channel : port.getIncomingDynamicChannels()) {
					if (channel.getTargetContext().equals(context)) {
						result += "\t\"" + channel.getSource().append(channel.getSourceContext()) + "\" -> \"" + port.append(context) + "\" [label = \"dynamic channel\", color = blue]; \n";
					}
				}
				
				if (port instanceof DefinitionPort) {
					ReferencePort port_proxy = Utils.getPortProxy(context, port);
					
					if (port_proxy != null) {
						result += "\t\"" + port_proxy.append(context.subList(0, context.size() - 1)) + "\" -> \"" + port.append(context) + "\" [label = \"forward\", color = gray]; \n";
					}
				}
				else if (port instanceof ReferencePort) {
					ReferencePort port_proxy = (ReferencePort) port;
					
					result += "\t\"" + port_proxy.getPortImplementation().append(port_proxy.getPortImplementation().getParent().append(context)) + "\" -> \"" + port_proxy.append(context) + "\" [label = \"forward\", color = gray];\n";
				}
			}
		}
		
		result += "}";
		
		return result;
	}
	
}
