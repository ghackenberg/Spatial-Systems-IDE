package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateChannel;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.ports.Port;

public class ChangeListenerMarkerPortDirection implements ChangeListener<Number> {
	
	private Port source;
	private Port target;
	private SyntacticMarker marker;
	
	public ChangeListenerMarkerPortDirection(Port source, Port target, SyntacticMarker marker) {
		this.source = source;
		this.target = target;
		this.marker = marker;
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		HelperEvaluateChannel.evaluateChannel(source, target, marker);
	}
}
