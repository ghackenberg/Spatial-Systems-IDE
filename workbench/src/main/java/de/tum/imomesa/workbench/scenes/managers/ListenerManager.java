package de.tum.imomesa.workbench.scenes.managers;

import de.tum.imomesa.core.listeners.ManagerListenerRemover;

public class ListenerManager {

	// data
	private static ManagerListenerRemover instance = new ManagerListenerRemover();

	public static ManagerListenerRemover getListenerRemover() {
		return instance;
	}

}
