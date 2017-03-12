package de.tum.imomesa.checker.helpers;

import de.tum.imomesa.checker.listeners.ChangeListenerMarkerPortDirection;
import de.tum.imomesa.checker.manager.ListenerRemover;
import de.tum.imomesa.checker.markers.SyntacticMarker;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;

public class HelperEvaluateChannel {
	
	// ***********************************************************************************
	// Channel
	// ***********************************************************************************
	protected static void evaluateNumbers(Number expected, Number current, SyntacticMarker marker) {
		if(current.equals(expected) == false) {
			HelperBasic.addMarker(marker);
		}
		else {
			HelperBasic.removeMarker(marker);
		}

	}
	public static void evaluateChannel(Port source, Port target, SyntacticMarker marker) {
		
		Element sourceComponent = source.getParent();
		Element targetComponent = target.getParent();

		Element sourceParent = sourceComponent.getParent();
		Element targetParent = targetComponent.getParent();
		
		// Case 1: both ports are in the components view as (different) children
		if(sourceComponent.equals(targetComponent) == false && sourceParent.equals(targetParent) && sourceParent instanceof Component && targetParent instanceof Component) {
			// source has to be output, target has to be input
			evaluateNumbers(Direction.OUTPUT.ordinal(), source.getDirection(), marker);
			evaluateNumbers(Direction.INPUT.ordinal(), target.getDirection(), marker);
		}
		
		// Case 2: both parents have the same parents
		// not allowed from UI
		
		// Case 3: Source is from component, target is from any sub component
		else if(targetParent instanceof Component && targetParent.equals(sourceComponent)) {
			// source has to be input, target has to be input
			evaluateNumbers(Direction.INPUT.ordinal(), source.getDirection(), marker);
			evaluateNumbers(Direction.INPUT.ordinal(), target.getDirection(), marker);
		}
		
		// Case 4: Source is from any sub component, target is from component
		else if(sourceParent instanceof Component && sourceParent.equals(targetComponent)) {
			// source has to be output, target has to be output
			evaluateNumbers(Direction.OUTPUT.ordinal(), source.getDirection(), marker);
			evaluateNumbers(Direction.OUTPUT.ordinal(), target.getDirection(), marker);
		}
		
		else {
			// combination should not occur
			throw new IllegalStateException();
		}
	}

	// ***********************************************************************************
	// Init evaluate channel
	// ***********************************************************************************
	public static void evaluateChannelInit(Port source, Port target, SyntacticMarker marker) {
		// evaluate initially
		evaluateChannel(source, target, marker);
		
		// add listener
		ListenerRemover.getListenerManager().addListener(source.directionProperty(), new ChangeListenerMarkerPortDirection(source, target, marker));
		ListenerRemover.getListenerManager().addListener(target.directionProperty(), new ChangeListenerMarkerPortDirection(source, target, marker));
	}

}
