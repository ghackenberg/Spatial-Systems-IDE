package de.tum.imomesa.workbench.commons.events;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class SimulationSelectionEvent extends SimulationEvent {

	public SimulationSelectionEvent() {

	}

	public SimulationSelectionEvent(List<Element> context, Component<?> component) {
		this.context.set(FXCollections.observableList(context));
		this.component.set(component);
	}

	public SimulationSelectionEvent(List<Element> extendedContext, Element element, List<Element> context,
			Component<?> component) {
		this.extendedContext.set(FXCollections.observableList(extendedContext));
		this.element.set(element);
		this.context.set(FXCollections.observableList(context));
		this.component.set(component);
	}

	private ListProperty<Element> extendedContext = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Element> getExtendedContext() {
		return extendedContext.get();
	}

	public ListProperty<Element> extendedContextProperty() {
		return extendedContext;
	}

	private ObjectProperty<Element> element = new SimpleObjectProperty<>();

	public Element getElement() {
		return element.get();
	}

	public ObjectProperty<Element> elementProperty() {
		return element;
	}

	private ListProperty<Element> context = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Element> getContext() {
		return context.get();
	}

	public ListProperty<Element> contextProperty() {
		return context;
	}

	private ObjectProperty<Component<?>> component = new SimpleObjectProperty<>();

	public Component<?> getComponent() {
		return component.get();
	}

	public ObjectProperty<Component<?>> componentProperty() {
		return component;
	}

}
