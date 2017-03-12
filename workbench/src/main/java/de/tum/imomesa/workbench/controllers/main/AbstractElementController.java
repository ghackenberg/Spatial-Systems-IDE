package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.model.Element;

public interface AbstractElementController<T extends Element> {
	
	/**
	 * Method to set the element of the view
	 * @param e: the selected element
	 */
	public abstract void setElement(T element);
}
