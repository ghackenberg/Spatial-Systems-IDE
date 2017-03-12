package de.tum.imomesa.checker.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.expressions.ObservationExpression;

public class ChangeListenerMarkerExpressionObservation implements ChangeListener<Observation> {
	
	// data
	private SyntacticMarker marker;
	private ObservationExpression parent;

	// constructor
	public ChangeListenerMarkerExpressionObservation(ObservationExpression parent, SyntacticMarker marker) {
		this.marker = marker;
		this.parent = parent;
	}
	
	@Override
	public void changed(ObservableValue<? extends Observation> observable, Observation oldValue, Observation newValue) {
		HelperEvaluateType.evaluateType(parent.getType(), newValue.getReadType(), marker);
		ListenerRemover.getListenerManager().addListener(newValue.readTypeProperty(), new ChangeListenerMarkerExpressionObservationType(parent.getType(), marker));
	}
}
