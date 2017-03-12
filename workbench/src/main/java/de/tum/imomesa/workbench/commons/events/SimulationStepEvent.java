package de.tum.imomesa.workbench.commons.events;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SimulationStepEvent extends SimulationEvent {
	
	public SimulationStepEvent() {
		
	}
	
	public SimulationStepEvent(int step) {
		this.step.set(step);
	}
	
	private IntegerProperty step = new SimpleIntegerProperty();
	
	public int getStep() {
		return step.get();
	}
	
	public IntegerProperty stepProperty() {
		return step;
	}
	
}
