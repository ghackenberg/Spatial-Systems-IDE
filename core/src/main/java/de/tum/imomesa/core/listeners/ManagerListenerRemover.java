package de.tum.imomesa.core.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("rawtypes")
public class ManagerListenerRemover {
	
	// ######################################################################
	// Data
	// ######################################################################
	HashMap<ObservableValue, List<ChangeListener>> memoryObservableValue = new HashMap<>();
	HashMap<ObservableList, List<ListChangeListener>> memoryObservableList = new HashMap<>();
	
	// ######################################################################
	// Public methods
	// ######################################################################
	@SuppressWarnings("unchecked")
	public void addListener(ObservableValue p, ChangeListener l) {
		p.addListener(l);
		
		if(memoryObservableValue.containsKey(p)) {
			memoryObservableValue.get(p).add(l);
		}
		else {
			memoryObservableValue.put(p, new ArrayList<>());
			memoryObservableValue.get(p).add(l);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addListener(ObservableList p, ListChangeListener l) {
		p.addListener(l);
		
		if(memoryObservableList.containsKey(p)) {
			memoryObservableList.get(p).add(l);
		}
		else {
			memoryObservableList.put(p, new ArrayList<>());
			memoryObservableList.get(p).add(l);
		}
	}
	

	@SuppressWarnings("unchecked")
	public void removeAllListener() {
		// remove all listener
		for( HashMap.Entry<ObservableValue, List<ChangeListener>> entry : memoryObservableValue.entrySet()) {

			ObservableValue key = entry.getKey();
			for(ChangeListener listener : entry.getValue()) {
				// remove listener
				key.removeListener(listener);
			}
		}
		
		// empty memory
		memoryObservableValue.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void removeAllListeners(ObservableValue p) {
		if(memoryObservableValue.containsKey(p)) {
			for(ChangeListener listener : memoryObservableValue.get(p)) {
				p.removeListener(listener);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeAllListeners(ObservableList p) {
		if(p != null && memoryObservableList.containsKey(p)) {
			for(ListChangeListener listener : memoryObservableList.get(p)) {
				p.removeListener(listener);
			}
		}
	}


}
