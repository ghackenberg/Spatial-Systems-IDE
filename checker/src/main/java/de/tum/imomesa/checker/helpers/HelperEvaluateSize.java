package de.tum.imomesa.checker.helpers;

import java.util.List;

import javafx.collections.ObservableList;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerSizeMax;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerSizeMin;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;

public class HelperEvaluateSize {
	
	// ***********************************************************************************
	// Evaluate Size
	// ***********************************************************************************
	public static <T extends Element> void evaluateMinSize(List<T> list, int minSize, SyntacticMarker marker) {
		if(list.size() < minSize) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	public static <T extends Element> void evaluateMaxSize(List<T> list, int maxSize, SyntacticMarker marker) {
		if(list.size() > maxSize) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	// ***********************************************************************************
	// Init evaluate size
	// ***********************************************************************************
	public static <T extends Element> void evaluateMinSizeInit(ObservableList<T> list, int minSize, SyntacticMarker marker) {
		evaluateMinSize(list, minSize, marker);
		ListenerRemover.getListenerManager().addListener(list, new ChangeListenerMarkerSizeMin<T>(list, minSize, marker));
	}
	
	public static <T extends Element> void evaluateMaxSizeInit(ObservableList<T> list, int maxSize, SyntacticMarker marker) {
		evaluateMaxSize(list, maxSize, marker);
		ListenerRemover.getListenerManager().addListener(list, new ChangeListenerMarkerSizeMax<T>(list, maxSize, marker));
	}
}