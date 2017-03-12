package de.tum.imomesa.workbench.scenes;

import java.util.List;

import de.tum.imomesa.model.Element;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

public class Configuration {

	public Configuration(BooleanProperty outline, BooleanProperty highlightSelected, ObjectProperty<Element> selected) {
		this.showOutline = outline;
		this.highlightSelected = highlightSelected;
		this.selectedElement = selected;
	}

	private BooleanProperty showOutline;

	public BooleanProperty showOutlineProperty() {
		return showOutline;
	}

	private BooleanProperty highlightSelected;

	public BooleanProperty highlightSelectedProperty() {
		return highlightSelected;
	}

	private ObjectProperty<Element> selectedElement;

	public Element getSelectedElement() {
		return selectedElement.get();
	}
	
	public ObjectProperty<Element> selectedElementProperty() {
		return selectedElement;
	}

	public boolean isElementSelected(List<Element> context, Element element) {
		return context.contains(selectedElement.get()) || element.equals(selectedElement.get());
	}

}
