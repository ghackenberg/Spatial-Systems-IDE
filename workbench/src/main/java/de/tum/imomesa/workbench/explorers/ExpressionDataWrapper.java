package de.tum.imomesa.workbench.explorers;


import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Guard;
import de.tum.imomesa.model.executables.Variable;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.properties.Property;

public class ExpressionDataWrapper {
	
	public static List<Class<?>> getType(Element e) {
		// Action
		if(e instanceof Action) {
			if(((Action)e).getObservation() != null ) {
				return ((Action)e).getObservation().getWriteType();
			}
			else {
				List<Class<?>> result = new ArrayList<Class<?>>();
				result.add(Object.class);
				return result;				
			}
		}
		
		// Property
		else if(e instanceof Property) {
			return ((Property)e).getWriteType();
		}
		
		// Variable
		else if(e instanceof Variable) {
			return ((Variable)e).getWriteType();
		}
		
		// Guard
		else if(e instanceof Guard) {
			// Guard always has to be boolean
			List<Class<?>> result = new ArrayList<Class<?>>();
			result.add(Boolean.class);
			return result;
		}
		
		// Port
		else if(e instanceof Port) {
			return ((Port)e).getWriteType();
		}
		
		else {
			throw new IllegalStateException();
		}
	}
}
