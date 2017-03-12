package de.tum.imomesa.integrator;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.integrator.events.ProgressChangeEvent;

public class Progress {

	private int total;
	private int processed;
	
	public Progress(int total) {
		this.total = total;
		this.processed = 0;
		
		EventBus.getInstance().publish(new ProgressChangeEvent(0.0));
	}
	
	public void increment() {
		processed++;
		
		EventBus.getInstance().publish(new ProgressChangeEvent(getPercentage()));
	}
	
	public double getPercentage() {
		return 1.0 * processed / total;
	}
	
}
