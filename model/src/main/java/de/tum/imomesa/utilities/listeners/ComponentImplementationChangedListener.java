package de.tum.imomesa.utilities.listeners;

import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.utilities.managers.StorageManager;
import javafx.collections.ListChangeListener;

public class ComponentImplementationChangedListener implements ListChangeListener<Port> {

	private ReferenceComponent proxy;

	public ComponentImplementationChangedListener(ReferenceComponent data) {
		this.proxy = data;
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends Port> c) {
		// collect (added/removed) port proxies
		while (c.next()) {
			if (c.wasPermutated()) {
				// ignore
			} else if (c.wasUpdated()) {
				// ignore
			} else {
				// Remove ports
				for (Port removed : c.getRemoved()) {
					for (int i = 0; i < proxy.getPorts().size(); i++) {
						if (((ReferencePort) proxy.getPorts().get(i)).getPortImplementation() == removed) {
							// TODO Is the following check really necessary?
							if (StorageManager.getInstance() != null) {
								StorageManager.getInstance().releaseElement(proxy.getPorts().remove(i--));
							}
						}
					}
				}
				// Add ports
				for (Port added : c.getAddedSubList()) {
					// Calculate exists
					boolean exists = false;
					boolean loading = false;
					for (int i = 0; i < proxy.getPorts().size(); i++) {
						DefinitionPort impl = ((ReferencePort) proxy.getPorts().get(i)).getPortImplementation();
						if (impl == null) {
							loading = true;
							break;
						}
						if (impl == added) {
							exists = true;
							break;
						}
					}
					// Create non-existing
					if (!loading && !exists) {
						proxy.getPorts().add(new ReferencePort(proxy, (DefinitionPort) added));
					}
				}
			}
		}
	}
}
