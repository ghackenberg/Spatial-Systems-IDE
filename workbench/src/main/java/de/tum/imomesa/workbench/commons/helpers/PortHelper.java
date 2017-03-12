package de.tum.imomesa.workbench.commons.helpers;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.ReferencePort;

public class PortHelper {
	
	/**
	 * This method returns a list with all items belonging to one port type
	 * This list will be added to the corresponding ListView in the UI
	 * @param ports: list of all ports (as they are not sorted)
	 * @param searchType: the type to be searched for
	 * @return List of all items belonging to the given port type
	 */
	public static List<Port> getPortsByType(List<? extends Port> ports, Class<?> searchType) {
		// create result list
		List<Port> result = new ArrayList<Port>();
		
		// go through all ports
		for(Port p : ports) {
			
			// get type of port object
			Class<?> type = p.getClass();
			
			// if the port is a proxy, get type of encapsulated object
			if(p instanceof ReferencePort) {
				type = ((ReferencePort)p).getPortImplementation().getClass();
			}
			
			while (Port.class.isAssignableFrom(type))
			{
				// check if type is the same
				if(searchType.equals(type)) {
					result.add(p);
				}
				
				// get superclass as long as not yet port
				type = type.getSuperclass();
			}
		}
		
		// return list
		return result;
	}
}
