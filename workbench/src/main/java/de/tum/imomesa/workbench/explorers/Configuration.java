package de.tum.imomesa.workbench.explorers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Configuration {

	private BooleanProperty requirements = new SimpleBooleanProperty();
	private BooleanProperty ports = new SimpleBooleanProperty();
	private BooleanProperty properties = new SimpleBooleanProperty();
	private BooleanProperty scenarios = new SimpleBooleanProperty();
	private BooleanProperty monitors = new SimpleBooleanProperty();
	private BooleanProperty components = new SimpleBooleanProperty();
	private BooleanProperty channels = new SimpleBooleanProperty();
	private BooleanProperty behaviors = new SimpleBooleanProperty();
	private BooleanProperty parts = new SimpleBooleanProperty();
	private BooleanProperty transforms = new SimpleBooleanProperty();

	public BooleanProperty requirementsProperty() {
		return requirements;
	}

	public BooleanProperty portsProperty() {
		return ports;
	}

	public BooleanProperty propertiesProperty() {
		return properties;
	}

	public BooleanProperty scenariosProperty() {
		return scenarios;
	}

	public BooleanProperty monitorsProperty() {
		return monitors;
	}

	public BooleanProperty componentsProperty() {
		return components;
	}

	public BooleanProperty channelsProperty() {
		return channels;
	}

	public BooleanProperty behaviorsProperty() {
		return behaviors;
	}

	public BooleanProperty partsProperty() {
		return parts;
	}

	public BooleanProperty transformsProperty() {
		return transforms;
	}

	public boolean getRequirements() {
		return requirements.get();
	}

	public boolean getPorts() {
		return ports.get();
	}

	public boolean getProperties() {
		return properties.get();
	}

	public boolean getScenarios() {
		return scenarios.get();
	}

	public boolean getMonitors() {
		return monitors.get();
	}

	public boolean getComponents() {
		return components.get();
	}

	public boolean getChannels() {
		return channels.get();
	}

	public boolean getBehaviors() {
		return behaviors.get();
	}

	public boolean getParts() {
		return parts.get();
	}

	public boolean getTransforms() {
		return transforms.get();
	}

}
