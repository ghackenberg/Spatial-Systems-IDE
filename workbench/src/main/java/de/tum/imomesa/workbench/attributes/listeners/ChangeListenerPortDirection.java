package de.tum.imomesa.workbench.attributes.listeners;

import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class ChangeListenerPortDirection implements ChangeListener<String> {
	
	// attributes
	private Port p;
	private ObservableList<String> options;
	
	// constructor
	public ChangeListenerPortDirection(Port p, ObservableList<String> options) {
		// save data
		this.p = p;
		this.options = options;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		// set data according to selected value
		if(newValue.equals(options.get(Direction.INPUT.ordinal()))) {
			p.setDirection(Direction.INPUT.ordinal());
		}
		else if(newValue.equals(options.get(Direction.OUTPUT.ordinal()))) {
			p.setDirection(Direction.OUTPUT.ordinal());
		}
		else {
			throw new IllegalStateException();
		}
	}
}
