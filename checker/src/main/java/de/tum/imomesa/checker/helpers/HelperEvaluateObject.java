package de.tum.imomesa.checker.helpers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerObject;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;

public class HelperEvaluateObject {
	
	// ***********************************************************************************
	// Evaluate object
	// ***********************************************************************************
	protected static <T extends Element> void evaluateObject(T e, SyntacticMarker marker) {
		if(e == null) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	public static <T extends Element> void evaluateObject(T e, SyntacticMarker marker, boolean condition) {
		if(condition == true) {
			evaluateObject(e, marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	// ***********************************************************************************
	// Init evaluate object
	// ***********************************************************************************
	public static <T extends Element> void evaluateObjectInit(ObjectProperty<T> e, SyntacticMarker marker) {
		evaluateObject(e.get(), marker);
		ListenerRemover.getListenerManager().addListener(e, new ChangeListenerMarkerObject<T>(marker, e.get()));
	}
	
	public static <T extends Element> void evaluateObjectInit(ObjectProperty<T> e, SyntacticMarker marker, BooleanBinding condition) {
		if(condition.get() == true) {
			evaluateObject(e.get(), marker);
		}
		ListenerRemover.getListenerManager().addListener(e, new ChangeListenerMarkerObject<T>(marker, condition, e.get()));
	}
	
}
