package de.tum.imomesa.checker.listeners;

import java.util.List;

import javafx.collections.ListChangeListener;
import de.tum.imomesa.checker.helpers.HelperEvaluateSize;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;

public class ChangeListenerMarkerSizeMin<T extends Element> implements ListChangeListener<T> {
	
	// data
	private SyntacticMarker marker;
	private int minSize;
	private List<T> list;
	
	// constructor
	public ChangeListenerMarkerSizeMin(List<T> list, int minSize, SyntacticMarker marker) {
		this.marker = marker;
		this.minSize = minSize;
		this.list = list;
	}
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> c) {
		HelperEvaluateSize.evaluateMinSize(list, minSize, marker);
	}
}
