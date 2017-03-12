package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.LifeMaterialPort;


public class LifeMaterialPortAddDialog extends SimpleAddDialog<LifeMaterialPort> {

	public LifeMaterialPortAddDialog() {
    	super("Add life material port", "Choose the type of the new port:", new EntryLifeMaterialPort(), new ExitLifeMaterialPort());
	}
	
}