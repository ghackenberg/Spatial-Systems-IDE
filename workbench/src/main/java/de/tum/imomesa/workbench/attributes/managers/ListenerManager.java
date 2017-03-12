package de.tum.imomesa.workbench.attributes.managers;

import de.tum.imomesa.core.listeners.ManagerListenerRemover;

public class ListenerManager {

	// data
	private static ManagerListenerRemover instance = new ManagerListenerRemover();

	public static ManagerListenerRemover getListenerManager() {
		return instance;
	}
}
