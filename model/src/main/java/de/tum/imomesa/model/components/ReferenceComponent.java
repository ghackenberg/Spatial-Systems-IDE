package de.tum.imomesa.model.components;

import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.utilities.listeners.ComponentImplementationChangedListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class ReferenceComponent extends Component<ReferencePort>
{

	private ListChangeListener<Port> listener = new ComponentImplementationChangedListener(this);
	
	public ReferenceComponent() {
		super();
		
		template.addListener(new ChangeListener<DefinitionComponent>() {
			@Override
			public void changed(ObservableValue<? extends DefinitionComponent> observable, DefinitionComponent oldValue, DefinitionComponent newValue) {
				if (oldValue != null) {
					// Remove listener
					oldValue.portsProperty().removeListener(listener);
				}
				if (newValue != null) {
					// Add listener
					newValue.portsProperty().addListener(listener);
				}
			}
		});
	}
	
	// Implementation Template
	
	private ObjectProperty<DefinitionComponent> template = new SimpleObjectProperty<>();
	
	public DefinitionComponent getTemplate()
	{
		return template.get();
	}
	public void setTemplate(DefinitionComponent c)
	{
		template.set(c);
	}
	public ObjectProperty<DefinitionComponent> templateProperty()
	{
		return template;
	}
}
