package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;

public class ComponentAddDialog extends SimpleAddDialog<Component<?>> {

	public ComponentAddDialog() {
    	super("Add component", "Choose the type of the new component:", new DefinitionComponent(), new ReferenceComponent());
	}
}