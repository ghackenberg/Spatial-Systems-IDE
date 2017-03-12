package de.tum.imomesa.simulator.events;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class StartEvent extends SimulationEvent {
	
	public StartEvent() {
		
	}
	
	public StartEvent(DefinitionComponent component, Scenario scenario) {
		this.component.set(component);
		this.scenario.set(scenario);
	}

	private ObjectProperty<DefinitionComponent> component = new SimpleObjectProperty<>();
	
	public DefinitionComponent getComponent() {
		return component.get();
	}
	
	public ObjectProperty<DefinitionComponent> componentProperty() {
		return component;
	}
	
	private ObjectProperty<Scenario> scenario = new SimpleObjectProperty<>();
	
	public Scenario getScenario() {
		return scenario.get();
	}
	
	public ObjectProperty<Scenario> scenarioProperty() {
		return scenario;
	}
	
}
