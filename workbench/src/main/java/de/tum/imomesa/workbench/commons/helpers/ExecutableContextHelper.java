package de.tum.imomesa.workbench.commons.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.properties.Property;

public class ExecutableContextHelper {
	
	// returns the data for an executable
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Observation> getObservationsToSet(Action a) {
		List<Observation> observations = new ArrayList<Observation>();

		// all are allowed to set own variables
		Executable executable = a.getFirstAncestorByType(Executable.class);
		if(executable != null) {
			observations.addAll(executable.getVariables());
			
			if(executable instanceof Scenario) {
				observations.addAll(((Scenario) executable).getPorts());
			}
		}
		else {
			throw new IllegalStateException();
		}
		
		
		if(a.getParent().getParent() instanceof Scenario) {
			// scenario is only allowed to set input ports
			Component comp = a.getFirstAncestorByType(Component.class);
			observations.addAll(ComponentContextHelper.getObservations(comp, Arrays.asList(Direction.INPUT, Direction.INPUT_OUTPUT)));
		}
		else if(a.getParent().getParent() instanceof Monitor) {
			// monitor is only allowed to set own variables
		}
		else if(a.getParent().getParent() instanceof Behavior) {
			// behavior is only allowed to set output ports
			DefinitionComponent comp = a.getFirstAncestorByType(DefinitionComponent.class);
			observations.addAll(ComponentContextHelper.getObservations(comp, Arrays.asList(Direction.OUTPUT, Direction.INPUT_OUTPUT)));
			for(Component<?> c : comp.getComponents()) {
				observations.addAll(ComponentContextHelper.getObservations(c, Arrays.asList(Direction.INPUT, Direction.INPUT_OUTPUT)));
			}
		}
		else {
			throw new IllegalStateException();
		}

		return observations;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Observation> getObservationsToRead(Element e) {
		List<Observation> observations = new ArrayList<Observation>();
		
		// add properties from label if available
		Property p = e.getFirstAncestorByType(Property.class);
		if(p != null) {
			observations.add(p);
		}
		
		// add variables from Executable
		Executable executable = e.getFirstAncestorByType(Executable.class);
		if(executable != null) {
			observations.addAll(executable.getVariables());
			
			if(executable instanceof Scenario) {
				observations.addAll(((Scenario) executable).getPorts());
			}
		}
		else {
			throw new IllegalStateException();
		}
		
		// add component observations
		DefinitionComponent comp = e.getFirstAncestorByType(DefinitionComponent.class);
		if(comp != null) {
			observations.addAll(ComponentContextHelper.getObservations(comp, Arrays.asList(Direction.values())));
			for(Component<?> c : comp.getComponents()) {
				observations.addAll(ComponentContextHelper.getObservations(c, Arrays.asList(Direction.values())));
			}
		}
		else {
			throw new IllegalStateException();
		}
		
		return observations;
	}

}
