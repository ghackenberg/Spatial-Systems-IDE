package de.tum.imomesa.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.ComponentProxy;
import de.tum.imomesa.model.elements.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.evaluators.ComponentEvaluator;
import de.tum.imomesa.simulator.evaluators.ComponentProxyEvaluator;
import de.tum.imomesa.simulator.evaluators.ScenarioEvaluator;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class Simulator extends Thread {
	
	// parameters
	
	private long maxtime;
	private long starttime;
	private long currenttime;
	
	// constructors
	
	public Simulator(Component component, Scenario scenario) {
		this(component, scenario, Long.MAX_VALUE);
	}
	
	public Simulator(Component component, Scenario scenario, long maxtime) {
		this.maxtime = maxtime;
		
		setComponent(component);
		setScenario(scenario);
		setMemory(new Memory());
		setStep(0);
		setRunning(true);
		setFinished(false);
		
		MarkerManager.get().init();
		
		try {
			memory.setProxy(0, new CopyOnWriteArraySet<ComponentProxy>());
			// Initialize component
			ComponentEvaluator comp_eval = new ComponentEvaluator(new ArrayList<>(), component, memory, 0);
			comp_eval.initialize();
			// Initialize scenario
			ScenarioEvaluator scen_eval = new ScenarioEvaluator(component.append(new ArrayList<>()), scenario, memory, 0);
			scen_eval.initialize();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	// component
	
	private Component component;
	
	public Component getComponent() {
		return component;
	}
	protected void setComponent(Component component) {
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
			starttime = System.currentTimeMillis();
			
			while(getRunning() && (currenttime = (System.currentTimeMillis() - starttime)) <= maxtime) {
				//System.out.println("Step: " + (getStep() + 1));
				
				// ***********************************************************************************
				// Initialize
				// ***********************************************************************************
				
				ThreadManager.getInstance().setRunning(true);
				
				// ***********************************************************************************
				// Collision Detection
				// ***********************************************************************************
				
				//System.out.println("Processing collisions");
				
				CollisionDetector.process(component, scenario, memory, getStep());
				
				// ***********************************************************************************
				// Start
				// ***********************************************************************************
				
				//System.out.println("Starting evaluators");
				
				// execute scenario
				ScenarioEvaluator scen_eval = new ScenarioEvaluator(component.append(new ArrayList<>()), scenario, memory, getStep() + 1);
				ComponentEvaluator comp_eval = new ComponentEvaluator(new ArrayList<>(), component, memory, getStep() + 1);

				List<ComponentProxyEvaluator> prox_evals = new ArrayList<>();
				
				for (ComponentProxy proxy : memory.getProxy(getStep())) {
					prox_evals.add(new ComponentProxyEvaluator(new ArrayList<>(), proxy, memory, getStep() + 1));
				}
				
				// Create thread
				scen_eval.createThread();
				comp_eval.createThread();
				for (ComponentProxyEvaluator prox_eval : prox_evals) {
					prox_eval.createThread();
				}
				
				// Start thread
				scen_eval.startThread();
				comp_eval.startThread();
				for (ComponentProxyEvaluator prox_eval : prox_evals) {
					prox_eval.startThread();
				}
				
				// ***********************************************************************************
				// Detect deadlocks
				// ***********************************************************************************
				
				//System.out.println("Detecting deadlocks");
				
				// Join unblocked threads
				ThreadManager.getInstance().join();
				// Check for blocked threads
				if (ThreadManager.getInstance().hasBlocked()) {
					// Mark blocked threads
					for (AbstractThread<?> thread : ThreadManager.getInstance().getThreads()) {
						if (ThreadManager.getInstance().hasBlockedThread(thread)) {
							AbstractThread<?> otherThread = ThreadManager.getInstance().getBlockedThread(thread);
							MarkerManager.get().addMarker(new ErrorMarker(thread.getElement().append(thread.getContext()), "Deadlock detected: " + otherThread, getStep() + 1));
						}
						if (ThreadManager.getInstance().hasBlockedObservation(thread)) {
							List<Element> observation = ThreadManager.getInstance().getBlockedObservation(thread);
							MarkerManager.get().addMarker(new ErrorMarker(thread.getElement().append(thread.getContext()), "Deadlock detected: " + observation, getStep() + 1));
						}
					}
				}
				// Terminate blocked threads
				ThreadManager.getInstance().setRunning(false);
				// Wake up blocked threads
				synchronized(memory) {
					memory.notifyAll();
				}
				
				// ***********************************************************************************
				// Join
				// ***********************************************************************************
				
				//System.out.println("Joining evaluators");
				
				// join evaluators
				comp_eval.joinThread();
				scen_eval.joinThread();
				for (ComponentProxyEvaluator prox_eval : prox_evals) {
					prox_eval.joinThread();
				}
				
				// ***********************************************************************************
				// Finalize
				// ***********************************************************************************
				
				//System.out.println("Finalizing evaluators");
		
				// set new array list of "unbound" components for next step
				memory.setProxy(getStep() + 1, new CopyOnWriteArraySet<>(memory.getProxy(getStep())));
				
				// finalize evaluators
				comp_eval.cleanup();
				scen_eval.cleanup();
				for (ComponentProxyEvaluator prox_eval : prox_evals) {
					prox_eval.cleanup();
				}
		
				// ***********************************************************************************
				// Evaluation
				// ***********************************************************************************
				
				//System.out.println("Checking finished");
				
				// simulation can be finished
				// - when an error occured
				// - when the final state is reached
		
				// check if an error occured
				if(MarkerManager.get().containsError() == true || scenario.getFinal().equals(memory.getLabel(scenario.append(component.append(new ArrayList<>())), getStep() + 1))) {
					// error occured
					setFinished(true);
					// Disable running flag
					setRunning(false);
					// Syso
					//System.out.println("Finished: " + (getStep() + 1));
				}
				
				// Update step
				setStep(getStep() + 1);
			}
			
			if (!getFinished() && currenttime > maxtime) {
				MarkerManager.get().addMarker(new TimeoutMarker(new ArrayList<>(), "Timeout!", getStep()));
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
