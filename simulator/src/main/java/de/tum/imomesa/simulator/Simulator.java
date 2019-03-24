package de.tum.imomesa.simulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.dispatchers.ObservationDispatcher;
import de.tum.imomesa.simulator.evaluators.DefinitionComponentEvaluator;
import de.tum.imomesa.simulator.evaluators.ReferenceComponentEvaluator;
import de.tum.imomesa.simulator.evaluators.ScenarioEvaluator;
import de.tum.imomesa.simulator.events.EndEvent;
import de.tum.imomesa.simulator.events.StartEvent;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.markers.errors.BlockedObservationError;
import de.tum.imomesa.simulator.markers.errors.BlockedThreadError;
import de.tum.imomesa.simulator.markers.errors.DeadlockError;
import de.tum.imomesa.simulator.threads.AbstractThread;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Simulator extends Thread {
	
	// parameters
	
	private long maxtime;
	private long starttime;
	private long currenttime;
	
	private String path;
	
	// constructors
	
	public Simulator(DefinitionComponent component, Scenario scenario) {
		this(component, scenario, "");
	}
	
	public Simulator(DefinitionComponent component, Scenario scenario, String path) {
		this(component, scenario, 100000, path);
	}

	public Simulator(DefinitionComponent component, Scenario scenario, long maxtime) {
		this(component, scenario, maxtime, "");
	}

	public Simulator(DefinitionComponent component, Scenario scenario, long maxtime, String path) {
		this.maxtime = maxtime;
		this.path = path;
		
		setComponent(component);
		setScenario(scenario);
		setMemory(new Memory());
		setStep(-1);
		setRunning(true);
		setFinished(false);
		
		Context.getInstance().setComponent(component);
		Context.getInstance().setScenario(scenario);
		
		MarkerManager.get().init();
	}
	
	// component
	
	private DefinitionComponent component;
	
	public DefinitionComponent getComponent() {
		return component;
	}
	protected void setComponent(DefinitionComponent component) {
		this.component = component;
	}
	
	// scenario
	
	private Scenario scenario;
	
	public Scenario getScenario() {
		return scenario;
	}
	protected void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	
	// memory
	
	private Memory memory;
	
	public Memory getMemory() {
		return memory;
	}
	protected void setMemory(Memory memory) {
		this.memory = memory;
	}
	
	// step

	private IntegerProperty step = new SimpleIntegerProperty(0);
	
	public int getStep() {
		return step.get();
	}
	protected void setStep(int step) {
		this.step.set(step);
	}
	public IntegerProperty stepProperty() {
		return step;
	}
	
	// running
	
	private BooleanProperty running = new SimpleBooleanProperty(true);
	
	public boolean getRunning() {
		return running.get();
	}
	public void setRunning(boolean running) {
		this.running.set(running);
	}
	public BooleanProperty runningProperty() {
		return running;
	}
	
	// finished
	
	private BooleanProperty finished = new SimpleBooleanProperty(false);
	
	public boolean getFinished() {
		return finished.get();
	}
	protected void setFinished(boolean finished) {
		this.finished.set(finished);
	}
	public BooleanProperty finishedProperty() {
		return finished;
	}
	
	// evaluateNext
	
	@Override
	public void run() {
		try {
			EventBus.getInstance().publish(new StartEvent(component, scenario));
			
			// Reset the entry material port count
			// TODO Fix here to enable multiple parallel simulations!
			ObservationDispatcher.getInstance().reset();

			// Measure
			starttime = System.currentTimeMillis();
			
			// Prepare
			memory.setProxy(0, new CopyOnWriteArraySet<ReferenceComponent>());
			memory.setAddedProxy(0, new CopyOnWriteArraySet<ReferenceComponent>());
			memory.setRemovedProxy(0, new CopyOnWriteArraySet<ReferenceComponent>());
			
			new DefinitionComponentEvaluator(new ArrayList<>(), component, memory, 0).prepare();
			new ScenarioEvaluator(component.append(new ArrayList<>()), scenario, memory, 0).prepare();
			
			// Loop
			while(getRunning() && (currenttime = (System.currentTimeMillis() - starttime)) <= maxtime) {
				ThreadManager.getInstance().setRunning(true);
				
				ScenarioEvaluator scen_eval;
				DefinitionComponentEvaluator comp_eval;
				List<ReferenceComponentEvaluator> prox_evals = new ArrayList<>();
				
				// Creating evaluators
				System.out.println("Step " + (getStep() + 1) + ": Creating evaluators");
				
				scen_eval = new ScenarioEvaluator(component.append(new ArrayList<>()), scenario, memory, getStep() + 1);
				comp_eval = new DefinitionComponentEvaluator(new ArrayList<>(), component, memory, getStep() + 1);
				for (ReferenceComponent proxy : memory.getProxy(getStep() + 1)) {
					prox_evals.add(new ReferenceComponentEvaluator(new ArrayList<>(), proxy, memory, getStep() + 1));
				}

				// Processing collisions
				System.out.println("Step " + (getStep() + 1) + ": Processing collisions");
				
				CollisionDetector.process(component, scenario, memory, getStep() + 1);
				
				// Creating threads
				System.out.println("Step " + (getStep() + 1) + ": Creating threads");
				
				scen_eval.createThread();
				comp_eval.createThread();
				for (ReferenceComponentEvaluator prox_eval : prox_evals) {
					prox_eval.createThread();
				}
				
				// Starting threads
				System.out.println("Step " + (getStep() + 1) + ": Starting threads");
				
				scen_eval.startThread();
				comp_eval.startThread();
				for (ReferenceComponentEvaluator prox_eval : prox_evals) {
					prox_eval.startThread();
				}
				
				
				// Detecting deadlocks
				System.out.println("Step " + (getStep() + 1) + ": Detecting deadlocks");
				
				// Join unblocked threads
				ThreadManager.getInstance().join();
				// Check for blocked threads
				if (ThreadManager.getInstance().hasBlocked()) {
					// Mark blocked threads
					for (AbstractThread<?> thread : ThreadManager.getInstance().getThreads()) {
						if (ThreadManager.getInstance().hasBlockedThread(thread)) {
							AbstractThread<?> otherThread = ThreadManager.getInstance().getBlockedThread(thread);
							MarkerManager.get().addMarker(new BlockedThreadError(thread.getElement().append(thread.getContext()), otherThread, getStep() + 1));
						}
						if (ThreadManager.getInstance().hasBlockedObservation(thread)) {
							List<Element> observation = ThreadManager.getInstance().getBlockedObservation(thread);
							MarkerManager.get().addMarker(new BlockedObservationError(thread.getElement().append(thread.getContext()), observation, getStep() + 1));
						}
					}
					// Print dot
					try {
						String name = path + System.currentTimeMillis() + "_deadlock";
						
						Writer writer = new FileWriter(new File(name + ".dot"));
						writer.write(ThreadManager.getInstance().toDot());
						writer.close();
						
						Process process = Runtime.getRuntime().exec("dot -Tpng -o " + name + ".png " + name + ".dot");
						process.waitFor();
						
						MarkerManager.get().addMarker(new DeadlockError(getStep() + 1, new File(name + ".png")));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// Terminate blocked threads
				ThreadManager.getInstance().setRunning(false);
				
				// Wake up blocked threads
				synchronized(memory) {
					memory.notifyAll();
				}
				
				
				// Joining threads
				System.out.println("Step " + (getStep() + 1) + ": Joining evaluators");
				
				comp_eval.joinThread();
				scen_eval.joinThread();
				for (ReferenceComponentEvaluator prox_eval : prox_evals) {
					prox_eval.joinThread();
				}
				
				// Cleaning up
				System.out.println("Step " + (getStep() + 1) + ": Cleaning up");
				
				comp_eval.cleanup();
				scen_eval.cleanup();
				for (ReferenceComponentEvaluator prox_eval : prox_evals) {
					prox_eval.cleanup();
				}
		
				// Checking status
				System.out.println("Step " + (getStep() + 1) + ": Checking status");
				
				if(MarkerManager.get().containsErrorMarker() == true || (scenario.getFinalLabel() != null && scenario.getFinalLabel().equals(memory.getLabel(scenario.append(component.append(new ArrayList<>())), getStep() + 2)))) {
					// error occured
					setFinished(true);
					// Disable running flag
					setRunning(false);
				}
				
				// set new array list of "unbound" components for next step
				CopyOnWriteArraySet<ReferenceComponent> proxies = new CopyOnWriteArraySet<ReferenceComponent>();
				
				for (ReferenceComponent proxy : memory.getProxy(getStep() + 1)) {
					if (!memory.getRemovedProxy(getStep() + 1).contains(proxy)) {
						proxies.add(proxy);
					}
				}
				for (ReferenceComponent proxy : memory.getAddedProxy(getStep() + 1)) {
					if (!memory.getRemovedProxy(getStep() + 1).contains(proxy)) {
						proxies.add(proxy);
					}
				}
				
				memory.setProxy(getStep() + 2, proxies);
				memory.setAddedProxy(getStep() + 2, new CopyOnWriteArraySet<ReferenceComponent>());
				memory.setRemovedProxy(getStep() + 2, new CopyOnWriteArraySet<ReferenceComponent>());
				
				// Update step
				setStep(getStep() + 1);
			}
			
			if (!getFinished() && currenttime > maxtime) {
				MarkerManager.get().addMarker(new TimeoutMarker(getStep()));
			}
			
			EventBus.getInstance().publish(new EndEvent());
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
