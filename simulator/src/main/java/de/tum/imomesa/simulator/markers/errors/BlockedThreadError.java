package de.tum.imomesa.simulator.markers.errors;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.threads.AbstractThread;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class BlockedThreadError extends BlockedError {

	public BlockedThreadError() {
		
	}
	
	public BlockedThreadError(List<Element> context, AbstractThread<?> other, int step) {
		super(context, "Waiting for thread: " + other.getName(), step);
		
		threadType.set(other.getClass().getName());
		threadContext.set(FXCollections.observableList(other.getContext()));
		threadElement.set(other.getElement());
	}
	
	private StringProperty threadType = new SimpleStringProperty();
	
	public StringProperty threadTypeProperty() {
		return threadType;
	}
	
	private ListProperty<Element> threadContext = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public ListProperty<Element> threadContextProperty() {
		return threadContext;
	}
	
	private ObjectProperty<Element> threadElement = new SimpleObjectProperty<>();
	
	public ObjectProperty<Element> threadElementProperty() {
		return threadElement;
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		
		BlockedThreadError marker = (BlockedThreadError) other;
		
		if (!threadContext.equals(marker.threadContext)) {
			return false;
		}
		if (!threadElement.equals(marker.threadElement)) {
			return false;
		}
		
		return true;
	}
	*/

}
