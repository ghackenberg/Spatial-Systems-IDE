package de.tum.imomesa.workbench.explorers.listeners;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.workbench.explorers.Configuration;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public class DefinitionComponentChangeListener implements ChangeListener<Boolean> {

	private TreeItem<Element> item;
	private Configuration configuration;

	private TreeItem<Element> requirements;
	private TreeItem<Element> ports;
	private TreeItem<Element> properties;
	private TreeItem<Element> scenarios;
	private TreeItem<Element> monitors;
	private TreeItem<Element> components;
	private TreeItem<Element> channels;
	private TreeItem<Element> behaviors;
	private TreeItem<Element> parts;
	private TreeItem<Element> transforms;

	public DefinitionComponentChangeListener(DefinitionComponent component, TreeItem<Element> item,
			Configuration configuration) {

		this.item = item;
		this.configuration = configuration;

		requirements = TreeItemBuilder.getInstance().transform(component, "Requirements", Requirement.class,
				component.requirementsProperty(), configuration);
		ports = TreeItemBuilder.getInstance().transform(component, "Ports", DefinitionPort.class,
				component.portsProperty(), configuration);
		properties = TreeItemBuilder.getInstance().transform(component, "Properties", Property.class,
				component.propertiesProperty(), configuration);
		scenarios = TreeItemBuilder.getInstance().transform(component, "Scenarios", Scenario.class,
				component.scenariosProperty(), configuration);
		monitors = TreeItemBuilder.getInstance().transform(component, "Monitors", Monitor.class,
				component.monitorsProperty(), configuration);
		components = TreeItemBuilder.getInstance().transform(component, "Components", Component.class,
				component.componentsProperty(), configuration);
		channels = TreeItemBuilder.getInstance().transform(component, "Channels", StaticChannel.class,
				component.channelsProperty(), configuration);
		behaviors = TreeItemBuilder.getInstance().transform(component, "Behaviors", Behavior.class,
				component.behaviorsProperty(), configuration);
		parts = TreeItemBuilder.getInstance().transform(component, "Parts", Part.class, component.partsProperty(),
				configuration);
		transforms = TreeItemBuilder.getInstance().transform(component, "Transforms", Transform.class,
				component.transformsProperty(), configuration);

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		update();
	}

	private void update() {
		item.getChildren().clear();

		if (configuration.getRequirements()) {
			item.getChildren().add(requirements);
		}
		if (configuration.getPorts()) {
			item.getChildren().add(ports);
		}
		if (configuration.getProperties()) {
			item.getChildren().add(properties);
		}
		if (configuration.getScenarios()) {
			item.getChildren().add(scenarios);
		}
		if (configuration.getMonitors()) {
			item.getChildren().add(monitors);
		}
		if (configuration.getComponents()) {
			item.getChildren().add(components);
		}
		if (configuration.getChannels()) {
			item.getChildren().add(channels);
		}
		if (configuration.getBehaviors()) {
			item.getChildren().add(behaviors);
		}
		if (configuration.getParts()) {
			item.getChildren().add(parts);
		}
		if (configuration.getTransforms()) {
			item.getChildren().add(transforms);
		}
	}

}
