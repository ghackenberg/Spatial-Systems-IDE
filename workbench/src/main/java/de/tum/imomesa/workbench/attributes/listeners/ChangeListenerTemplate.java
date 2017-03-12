package de.tum.imomesa.workbench.attributes.listeners;

import de.tum.imomesa.model.components.DefinitionComponent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ChangeListenerTemplate implements ChangeListener<DefinitionComponent> {
	
	private ObjectProperty<DefinitionComponent> componentProperty;
	
	public ChangeListenerTemplate(ObjectProperty<DefinitionComponent> componentProperty) {
		this.componentProperty = componentProperty;
	}

	@Override
	public void changed(ObservableValue<? extends DefinitionComponent> observable, DefinitionComponent oldValue, DefinitionComponent newValue) {
		componentProperty.setValue(newValue);
	}

}
