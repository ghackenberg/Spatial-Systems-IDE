package de.tum.imomesa.checker.managers;

import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.core.listeners.ManagerListenerRemover;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ListenerRemover {
	
	// data
	private static ManagerListenerRemover instance = new ManagerListenerRemover();

	public static ManagerListenerRemover getListenerManager() {
		return instance;
	}
	
	public interface ValueProcessor<T> {
		public boolean process(T value);
	}
	
	private static <T> void addListener(javafx.beans.property.Property<T> property, ChangeListener<T> listener) {
		ListenerRemover.getListenerManager().addListener(property, listener);
		listener.changed(property, null, property.getValue());
	}
	
	public static <T> void addListener(javafx.beans.property.Property<T> property, ValueProcessor<T> processor, SyntacticMarker marker) {
		addListener(property, (observable, oldValue, newValue) -> {
			if (processor.process(newValue)) {
				SyntacticMarkerManager.getInstance().removeMarker(marker);
			} else {
				SyntacticMarkerManager.getInstance().addMarker(marker);
			}
		});
	}
	
	private static <T> void addListListener(ListProperty<T> property, ListChangeListener<T> listener) {
		property.addListener(listener);
		listener.onChanged(null);
	}
	
	public static <T> void addListListener(ListProperty<T> property, ValueProcessor<ObservableList<? extends T>> processor, SyntacticMarker marker) {
		addListListener(property, change -> {
			if (processor.process(property)) {
				SyntacticMarkerManager.getInstance().removeMarker(marker);
			} else {
				SyntacticMarkerManager.getInstance().addMarker(marker);
			}
		});
	}
	
}
