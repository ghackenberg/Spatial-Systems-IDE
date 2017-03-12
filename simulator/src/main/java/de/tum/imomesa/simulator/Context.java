package de.tum.imomesa.simulator;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Context {

	private static Context INSTANCE = new Context();
	
	public static Context getInstance() {
		return INSTANCE;
	}
	
	private ObjectProperty<DefinitionComponent> component = new SimpleObjectProperty<>();
	
	public DefinitionComponent getComponent() {
		return component.get();
	}
	
	public void setComponent(DefinitionComponent component) {
		this.component.set(component);
	}
	
	public ObjectProperty<DefinitionComponent> componentProperty() {
		return component;
	}
	
	private ObjectProperty<Scenario> scenario = new SimpleObjectProperty<>();
	
	public Scenario getScenario() {
		return scenario.get();
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario.set(scenario);
	}
	
	public ObjectProperty<Scenario> scenarioProperty() {
		return scenario;
	}
	
}
