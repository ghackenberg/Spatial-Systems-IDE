package de.tum.imomesa.workbench.commons.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.properties.Property;

public class ContextHelper {
	
	/**
	 * This class retrieves the observations for an expression
	 * @param p Port
	 * @return the observation list
	 */
	public static List<Observation> getObservations(ObservationExpression exp) {
		// possibilities are
		//    - ports
		//    - action
		//    - property
		//    - variable
		//    - guard
		
		List<Observation> observations = new ArrayList<Observation>();
		
		@SuppressWarnings("rawtypes")
		Executable executable = exp.getFirstAncestorByType(Executable.class);
		if(executable != null) {
			observations = ExecutableContextHelper.getObservationsToRead(exp.getParent());
		}
		else {
			// expression is beneath component
			// expression is from port / property
			DefinitionComponent comp = exp.getFirstAncestorByType(DefinitionComponent.class);
			Property prop = exp.getFirstAncestorByType(Property.class);
			Port port = exp.getFirstAncestorByType(Port.class);
			
			if(comp != null) {
				if(prop != null) {
					observations = ComponentContextHelper.getObservations(comp, Arrays.asList(Direction.values()));
					observations.add(prop);
					for(Component<?> c : comp.getComponents()) {
						observations.addAll(ComponentContextHelper.getObservations(c, Arrays.asList(Direction.values())));
					}
				}
				else if(port != null) {
					observations = ComponentContextHelper.getObservations(comp, Arrays.asList(Direction.values()));
					for(Component<?> c : comp.getComponents()) {
						observations.addAll(ComponentContextHelper.getObservations(c, Arrays.asList(Direction.values())));
					}
				}
				else {
					throw new IllegalStateException();
				}
			}
			else {
				throw new IllegalStateException();
			}
		}

		return filterResults(exp, observations);
	}
	
	// ######################################################################################
	// filter results depending on expression type
	// ######################################################################################
	private static List<Observation> filterResults(Expression exp, List<Observation> observations) {
		// For further information, refer to http://www.ralfebert.de/archive/java/isassignablefrom/
		// filter results
		List<Observation> result = new ArrayList<Observation>();
		for(Observation o : observations) {
			if(exp.getType().isAssignableFrom(o.getReadType())) {
				result.add(o);
			}
		}
		
		return result;
	}
}
