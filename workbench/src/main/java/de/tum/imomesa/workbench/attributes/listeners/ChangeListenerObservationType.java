package de.tum.imomesa.workbench.attributes.listeners;

import java.util.ArrayList;
import java.util.Arrays;

import de.tum.imomesa.model.Observation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ChangeListenerObservationType<T extends Observation> implements ChangeListener<Class<?>> {
	
	// attribute
	// should be DataPort or Variable
	private T data;
	
	// constructor
	public ChangeListenerObservationType(T data) {
		this.data = data;
	}

	@Override
	public void changed(ObservableValue<? extends Class<?>> observable, Class<?> oldValue, Class<?> newValue) {
		if(oldValue != newValue) {
			data.setReadType(newValue);
			data.setWriteType(new ArrayList<>(Arrays.asList(newValue)));
		}
	}
}
