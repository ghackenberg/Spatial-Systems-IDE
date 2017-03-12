package de.tum.imomesa.analyzer.helpers;

import java.util.List;

import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.properties.Property;

public class Mapper {

	public static final Class<?>[] FEATURES_EXCLUDED = { Workspace.class, Project.class, Point.class };
	public static final Class<?>[] FEATURES_INCLUDED = { Component.class, Requirement.class, Port.class, Property.class,
			Scenario.class, Monitor.class, Behavior.class, Part.class };

	public static Class<?> mapFeature(Change change) {
		Element iterator = (Element) change.getObject();
		return mapFeature(iterator);
	}

	public static Class<?> mapFeature(Element iterator) {
		while (iterator != null) {
			if (iterator instanceof Property && iterator.getParent() instanceof Label) {
				// ignore
			} else if (iterator instanceof Port && iterator.getParent() instanceof Scenario) {
				// ignore
			} else if (iterator instanceof Port && iterator.getParent() instanceof Port) {
				// ignore
			} else {
				for (Class<?> type : FEATURES_EXCLUDED) {
					if (type.isAssignableFrom(iterator.getClass())) {
						return null;
					}
				}
				for (Class<?> type : FEATURES_INCLUDED) {
					if (type.isAssignableFrom(iterator.getClass())) {
						return type;
					}
				}
			}
			iterator = iterator.getParent();
		}

		return null;
	}

	public static DefinitionComponent mapComponent(Change change) {
		Element element = (Element) change.getObject();
		return mapComponent(element);
	}

	public static DefinitionComponent mapComponent(Element element) {
		Project project = element.getFirstAncestorByType(Project.class);

		List<DefinitionComponent> components = element.getAncestorsByType(DefinitionComponent.class);

		if (components.size() > 0) {
			if (components.get(components.size() - 1).equals(project.getComponent())) {
				return components.get(Math.max(components.size() - 2, 0));
			} else {
				if (components.get(components.size() - 1).getName().equals("Basket cylinder")) {
					return null;
				} else {
					return components.get(components.size() - 1);
				}
			}
		} else {
			return null;
		}
	}

}
