package de.tum.imomesa.workbench.commons.events;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SimulationStartEvent extends SimulationEvent {
	
	public SimulationStartEvent() {
		
	}
	
	public SimulationStartEvent(DefinitionComponent component, Scenario scenario) {
		this.component.set(component);
		this.scenario.set(scenario);
	}
	
	private ObjectProperty<DefinitionComponent> component = new SimpleObjectProperty<>();
	
	public ObjectProperty<DefinitionComponent> componentProperty() {
		return component;
	}
	
	private ObjectProperty<Scenario> scenario = new SimpleObjectProperty<>();
	
	public ObjectProperty<Scenario> scenarioProperty() {
		return scenario;
	}

}
