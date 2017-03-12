package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.transforms.TranslationTransform;

public class TransformAddDialog extends SimpleAddDialog<Transform> {
	
    public TransformAddDialog() {
    	super("Add transform", "Choose the type of the new transform:", new TranslationTransform(), new RotationTransform());
	}
    
}
