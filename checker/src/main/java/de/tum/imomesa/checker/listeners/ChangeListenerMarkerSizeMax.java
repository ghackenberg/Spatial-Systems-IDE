package de.tum.imomesa.checker.listeners;

import java.util.List;

import javafx.collections.ListChangeListener;
import de.tum.imomesa.checker.helpers.HelperEvaluateSize;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;

public class ChangeListenerMarkerSizeMax<T extends Element> implements ListChangeListener<T> {
	
	// data
	private SyntacticMarker marker;
	private int maxSize;
	private List<T> list;
	
	// constructor
	public ChangeListenerMarkerSizeMax(List<T> list, int maxSize, SyntacticMarker marker) {
		this.marker = marker;
		this.maxSize = maxSize;
		this.list = list;
	}
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> c) {
		HelperEvaluateSize.evaluateMaxSize(list, maxSize, marker);
	}
}
