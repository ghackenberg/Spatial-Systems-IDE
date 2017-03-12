package de.tum.imomesa.simulator.markers;

import java.util.List;

import de.tum.imomesa.core.markers.Marker;
import de.tum.imomesa.model.Element;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public abstract class SimulationMarker extends Marker {

	public SimulationMarker() {
		
	}
	
	public SimulationMarker(List<Element> context, String message, int step) {
		setContext(context);
		setMessage(message);
		setStep(step);
	}

	// Context
	private ListProperty<Element> context = new SimpleListProperty<Element>(FXCollections.observableArrayList());

	public List<Element> getContext() {
		return context.getValue();
	}

	public void setContext(List<Element> context) {
		this.context.set(FXCollections.observableArrayList(context));
	}

	public ListProperty<Element> contextProperty() {
		return context;
	}

	// Message
	private StringProperty message = new SimpleStringProperty();

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public StringProperty messageProperty() {
		return message;
	}

	// Step
	private IntegerProperty step = new SimpleIntegerProperty();

	public int getStep() {
		return step.getValue();
	}

	public void setStep(int step) {
		this.step.set(step);
	}

	public IntegerProperty stepProperty() {
		return step;
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		
		SimulationMarker marker = (SimulationMarker) other;
		
		if (!context.equals(marker.context)) {
			return false;
		}
		
		return true;
	}
	*/
	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}

}
