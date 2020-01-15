package de.tum.imomesa.workbench.controllers;

import de.tum.imomesa.model.Element;

public interface AbstractEditorChildController<T extends Element> {
	
	/**
	 * Method to set the element of the view
	 * @param e: the selected element
	 */
	public abstract void setRootElement(T element);
	
	public abstract void setSelectedElement(Element element);
}
