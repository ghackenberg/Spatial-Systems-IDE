package de.tum.imomesa.workbench.simulations.threads;

import de.tum.imomesa.simulator.Simulator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class AnimationThread extends Thread {
	
	//
	// Fields
	//

	private Simulator simulator;
	private Slider slider;
	private Spinner<Integer> spinner;
	
	//
	// Constructors
	//
	
	public AnimationThread(Simulator simulator, Slider slider, Spinner<Integer> spinner) {
		this.simulator = simulator;
		this.slider = slider;
		this.spinner = spinner;
	}
	
	//
	// Propeties
	//
	
	// Running
	
	private BooleanProperty running = new SimpleBooleanProperty(true);
	
	public void setRunning(boolean running) {
		this.running.set(running);
		
		synchronized (this) {
			notify();
		}
	}
	
	public boolean getRunning() {
		return running.get();
	}
	
	public BooleanProperty runningProperty() {
		return running;
	}
	
	// Paused
	
	private BooleanProperty paused = new SimpleBooleanProperty(false);
	
	public void setPaused(boolean paused) {
		this.paused.set(paused);
		
		synchronized (this) {
			notify();
		}
	}
	
	public boolean getPaused() {
		return paused.get();
	}
	
	public BooleanProperty pausedProperty() {
		return paused;
	}
	
	//
	// Methods
	//
	
	@Override
	public void run() {
		try {
			Thread.sleep(spinner.getValue());
			
			while (getRunning()) {
				while (getRunning() && getPaused()) {
					synchronized (this) {
						wait();
					}
				}
				if (getRunning()) {
					if (slider.getValue() + 1 <= slider.getMax()) {
						slider.setValue(Math.min(slider.getMax(), slider.getValue() + 1));
					}
					else if (simulator.getFinished()) {
						setPaused(true);
					}
					
					Thread.sleep(spinner.getValue());
				}
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
