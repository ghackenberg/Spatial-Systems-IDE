package de.tum.imomesa.integrator.events;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ProgressChangeEvent extends IntegratorEvent {
	
	public ProgressChangeEvent() {
		
	}
	
	public ProgressChangeEvent(double progress) {
		this.progress.set(progress);
	}
	
	private DoubleProperty progress = new SimpleDoubleProperty();
	
	public double getProgress() {
		return progress.get();
	}
	
	public DoubleProperty progressProperty() {
		return progress;
	}

}
