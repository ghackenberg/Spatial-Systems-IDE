package de.tum.imomesa.integrator.reports;

import java.util.List;

import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public abstract class AbstractReport<T extends NamedElement> {

	private T element;
	private String name;
	private String description;
	private long start;
	private long stop;
	private long duration;
	
	public AbstractReport(T element, String name, String description) {
		this.element = element;
		this.name = name;
		this.description = description;
		
		start = System.currentTimeMillis();
	}
	
	public T getElement() {
		return element;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void stop() {
		stop = System.currentTimeMillis();
		duration = stop - start;
	}
	
	public abstract boolean getSuccessFlag();
	
	public abstract int getSuccessCount();
	public abstract int getFailureCount();
	public abstract int getTimeoutCount();
	
	public int getTotalCount() {
		return getSuccessCount() + getFailureCount() + getTimeoutCount();
	}
	
	public abstract List<SimulationMarker> getMarkers();
	
	public long getStart() {
		return start;
	}
	public long getStop() {
		return stop;
	}
	public long getDuration() {
		return duration;
	}
	
}
