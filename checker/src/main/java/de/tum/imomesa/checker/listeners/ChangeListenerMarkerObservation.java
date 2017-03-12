package de.tum.imomesa.checker.listeners;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.tum.imomesa.checker.helpers.HelperEvaluateType;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.ports.DataPort;

public class ChangeListenerMarkerObservation implements ChangeListener<Observation> {
	
	// data
	private SyntacticMarker marker;
	private ObjectProperty<Expression> expressions;

	// constructor
	public ChangeListenerMarkerObservation(ObjectProperty<Expression> expressions, SyntacticMarker marker) {
		this.marker = marker;
		this.expressions = expressions;
	}
	
	@Override
	public void changed(ObservableValue<? extends Observation> observable, Observation oldValue, Observation newValue) {
		if(newValue != null) {
			
			// add listener on changes of expression
			expressions.addListener(new ChangeListenerMarkerExpression(newValue.writeTypeProperty(), marker));
			
			// add listener on changes of write type property
			if(newValue instanceof Variable || newValue instanceof DataPort) {
				newValue.writeTypeProperty().addListener(new ChangeListenerMarkerClass(expressions, marker));
			}

			if(expressions.get() != null) {
				HelperEvaluateType.evaluateType(newValue.getWriteType(), expressions.get().getType(), marker);
			}
		}
	}
}
