package de.tum.imomesa.checker.manager;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.tum.imomesa.checker.events.MarkerAddEvent;
import de.tum.imomesa.checker.events.MarkerRemoveEvent;
import de.tum.imomesa.checker.markers.SyntacticError;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;

public class SyntacticMarkerManager {

	// ***********************************************************************************
	// Singleton implementation
	// ***********************************************************************************
	// single instance
	private static SyntacticMarkerManager instance;
	
	// private constructor
	private SyntacticMarkerManager() {
		// do nothing
	}
	
	// get instance
	public static SyntacticMarkerManager getInstance() {
	    if (SyntacticMarkerManager.instance == null) {
	    	SyntacticMarkerManager.instance = new SyntacticMarkerManager ();
	      }
	    return SyntacticMarkerManager.instance;
	}
	
	// ***********************************************************************************
	// Attributes
	// ***********************************************************************************
	private ObservableList<SyntacticMarker> markers = FXCollections.observableArrayList();
	
	// ***********************************************************************************
	// Methods
	// ***********************************************************************************
	public void addMarker(SyntacticMarker marker) {
		if(!markers.contains(marker)) {
			markers.add(marker);
			EventBus.getInstance().publish(new MarkerAddEvent(marker));
		}
	}
	
	public void removeMarker(SyntacticMarker marker) {
		if(markers.contains(marker)) {	
			markers.remove(marker);
			EventBus.getInstance().publish(new MarkerRemoveEvent(marker));
		}
	}

	public void removeMarkers(Element e) {
		List<SyntacticMarker> markersToRemove = new ArrayList<>();
		
		// on exit: remove markers
		for(SyntacticMarker markerElement : markers) {
			if(markerElement.getElement().equals(e)) {
				markersToRemove.add(markerElement);
				EventBus.getInstance().publish(new MarkerRemoveEvent(markerElement));
			}
		}
		
		// remove from list
		markers.removeAll(markersToRemove);
	}
	
	public ObservableList<SyntacticMarker> getMarkers() {
		return markers;
	}
	
	public boolean containsError() {
		for(SyntacticMarker m : markers) {
			if(m instanceof SyntacticError) {
				return true;
			}
		}
		
		return false;
	}

}