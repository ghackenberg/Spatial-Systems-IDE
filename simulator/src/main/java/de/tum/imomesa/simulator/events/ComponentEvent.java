package de.tum.imomesa.simulator.events;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class ComponentEvent extends SimulationEvent {
	
	public ComponentEvent() {
		
	}
	
	public ComponentEvent(List<Element> context, LifeMaterialPort port, int step, ReferenceComponent component) {
		this.context.set(FXCollections.observableList(context));
		this.port.set(port);
		this.step.set(step);
		this.component.set(component);
	}
	
	private ListProperty<Element> context = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public ListProperty<Element> contextProperty() {
		return context;
	}
	
	private ObjectProperty<LifeMaterialPort> port = new SimpleObjectProperty<>();
	
	public ObjectProperty<LifeMaterialPort> portProperty() {
		return port;
	}
	
	private IntegerProperty step = new SimpleIntegerProperty();
	
	public IntegerProperty stepProperty() {
		return step;
	}
	
	private ObjectProperty<ReferenceComponent> component = new SimpleObjectProperty<>();
	
	public ObjectProperty<ReferenceComponent> componentProperty() {
		return component;
	}
	
}
