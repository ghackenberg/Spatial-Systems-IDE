package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.properties.ConstraintProperty;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.properties.QualityProperty;

public class PropertyAddDialog extends SimpleAddDialog<Property>  {
	
	public PropertyAddDialog() {
    	super("Add property", "Choose the type of the new property:", new ConstraintProperty(), new QualityProperty());
	}
	
}