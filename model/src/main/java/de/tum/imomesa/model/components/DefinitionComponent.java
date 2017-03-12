package de.tum.imomesa.model.components;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class DefinitionComponent extends Component<DefinitionPort> {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Requirement r : requirements) {
			r.accept(visitor);
		}
		for (Property p : properties) {
			p.accept(visitor);
		}
		for (Scenario s : scenarios) {
			s.accept(visitor);
		}
		for (Monitor m : monitors) {
			m.accept(visitor);
		}
		for (Component<?> c : components) {
			c.accept(visitor);
		}
		for (Behavior b : behaviors) {
			b.accept(visitor);
		}
		for (Part p : parts) {
			p.accept(visitor);
		}
	}

	// Requirements

	private final ListProperty<Requirement> requirements = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<Requirement> getRequirements() {
		return requirements.get();
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements.set(FXCollections.observableList(requirements));
	}

	@Cascading
	public ListProperty<Requirement> requirementsProperty() {
		return requirements;
	}

	// Properties

	private final ListProperty<Property> properties = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Property> getProperties() {
		return properties.get();
	}

	public void setProperties(List<Property> properties) {
		this.properties.set(FXCollections.observableList(properties));
	}

	@Cascading
	public ListProperty<Property> propertiesProperty() {
		return properties;
	}

	// Scenarios

	private final ListProperty<Scenario> scenarios = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Scenario> getScenarios() {
		return scenarios.get();
	}

	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios.set(FXCollections.observableList(scenarios));
	}

	@Cascading
	public ListProperty<Scenario> scenariosProperty() {
		return scenarios;
	}

	// Monitors

	private final ListProperty<Monitor> monitors = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Monitor> getMonitors() {
		return monitors.get();
	}

	public void setMonitors(List<Monitor> monitors) {
		this.monitors.set(FXCollections.observableList(monitors));
	}

	@Cascading
	public ListProperty<Monitor> monitorsProperty() {
		return monitors;
	}

	// Components

	private final ListProperty<Component<?>> components = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Component<?>> getComponents() {
		return components.get();
	}

	public void setComponents(List<Component<?>> components) {
		this.components.set(FXCollections.observableList(components));
	}

	@Cascading
	public ListProperty<Component<?>> componentsProperty() {
		return components;
	}
	
	// Channels
	// TODO Use channels list!
	
	private final ListProperty<StaticChannel> channels = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public List<StaticChannel> getChannels() {
		return channels.get();
	}
	
	public void setChannels(List<StaticChannel> channels) {
		this.channels.set(FXCollections.observableList(channels));
	}
	
	@Cascading
	public ListProperty<StaticChannel> channelsProperty() {
		return channels;
	}

	// Behaviors

	private final ListProperty<Behavior> behaviors = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Behavior> getBehaviors() {
		return behaviors.get();
	}

	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors.set(FXCollections.observableList(behaviors));
	}

	@Cascading
	public ListProperty<Behavior> behaviorsProperty() {
		return behaviors;
	}

	// Parts

	private final ListProperty<Part> parts = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Part> getParts() {
		return parts.get();
	}

	public void setParts(List<Part> parts) {
		this.parts.set(FXCollections.observableList(parts));
	}

	@Cascading
	public ListProperty<Part> partsProperty() {
		return parts;
	}

}
