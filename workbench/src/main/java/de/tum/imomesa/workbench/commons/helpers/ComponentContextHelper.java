package de.tum.imomesa.workbench.commons.helpers;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;

public class ComponentContextHelper {
	
	// gets all Observations for a context
	// parametrized by the PortDirection
	
	public static List<Observation> getObservations(Component<?> comp, List<Direction> directions) {
		List<Observation> observations = new ArrayList<Observation>();
		
		// get all ports of component
		for(Port p : comp.getPorts()) {
			
			// check on direction
			if(directions.contains(Direction.values()[p.getDirection()])) {
				observations.add(p);
			}
			
			// add ports of materialport (only if direction suites)
			// direction has to be the opposite of the input directions
			List<Direction> directionMatBindingPort = new ArrayList<>(directions);
			if(directionMatBindingPort.contains(Direction.INPUT)) {
				directionMatBindingPort.remove(Direction.INPUT);
				directionMatBindingPort.add(Direction.OUTPUT);
			}
			if(directionMatBindingPort.contains(Direction.OUTPUT)) {
				directionMatBindingPort.remove(Direction.OUTPUT);
				directionMatBindingPort.add(Direction.INPUT);
			}

			if(p instanceof InteractionMaterialPort) {
				for(Port pPort : ((InteractionMaterialPort) p).getPorts()) {
					if(directionMatBindingPort.contains(Direction.values()[pPort.getDirection()])) {
						observations.add(pPort);
					}
				}				
			}
		}

		return observations;
	}
}
