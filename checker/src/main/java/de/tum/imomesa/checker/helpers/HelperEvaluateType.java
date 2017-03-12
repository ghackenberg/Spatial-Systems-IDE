package de.tum.imomesa.checker.helpers;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerClass;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerExpression;
import de.tum.imomesa.checker.listeners.ChangeListenerMarkerObservation;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.ports.DataPort;

public class HelperEvaluateType {

	// ***********************************************************************************
	// Evaluate Type
	// ***********************************************************************************
	public static void evaluateType(Class<?> parentType, Class<?> childType, SyntacticMarker marker) {
		// childType instanceof parentType
		if(parentType.isAssignableFrom(childType) == false) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}
	}
	
	public static void evaluateType(List<Class<?>> parentTypes, Class<?> childType, SyntacticMarker marker) {
		for(Class<?> parentType : parentTypes) {
			if(parentType.isAssignableFrom(childType) == true) {
				HelperBasic.removeMarker(marker);
				return;
			}
		}
		
		HelperBasic.addMarker(marker);
	}
	
	// ***********************************************************************************
	// Init evaluate Type
	// ***********************************************************************************
	// Observations: Properties, Port, Variable
	// Others: Action (has an observation)
	// Others: Guard (is boolean)
	
	public static void evaluateTypeObservationInit(ObjectProperty<Observation> o, ObjectProperty<Expression> expressions, SyntacticMarker marker) {
		// observation can change -> add listener
		// -> add listener on observation type as well (if necessary)
		ListenerRemover.getListenerManager().addListener(o, new ChangeListenerMarkerObservation(expressions, marker));
		
		evaluateTypeObservationInit(o.get(), expressions, marker);
	}
	
	public static void evaluateTypeObservationInit(Observation o, ObjectProperty<Expression> expressions, SyntacticMarker marker) {
		if(o != null && expressions.get() != null) {
			evaluateType(o.getWriteType(), expressions.get().getType(), marker);
		}
		
		// type of observation can change -> add listener
		if(o != null && (o instanceof Variable || o instanceof DataPort)) {
			o.writeTypeProperty().addListener(new ChangeListenerMarkerClass(expressions, marker));
		}
		
		// expression can change -> add listener
		if(o != null) {
			ListenerRemover.getListenerManager().addListener(expressions, new ChangeListenerMarkerExpression(o.writeTypeProperty(), marker));
		}
	}
	
	public static void evaluateTypeObservationInit(List<Class<?>> types, ObjectProperty<Expression> expressions, SyntacticMarker marker) {
		if(expressions.get() != null) {
			evaluateType(types, expressions.get().getType(), marker);
		}

		// expression can change -> add listener
		ListenerRemover.getListenerManager().addListener(expressions, new ChangeListenerMarkerExpression(new SimpleListProperty<Class<?>>(FXCollections.observableList(types)), marker));
	}
}
