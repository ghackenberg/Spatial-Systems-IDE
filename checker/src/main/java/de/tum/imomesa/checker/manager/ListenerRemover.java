package de.tum.imomesa.checker.manager;

import de.tum.imomesa.core.listeners.ManagerListenerRemover;

public class ListenerRemover {
	
	// data
	private static ManagerListenerRemover instance = new ManagerListenerRemover();

	public static ManagerListenerRemover getListenerManager() {
		return instance;
	}
}
