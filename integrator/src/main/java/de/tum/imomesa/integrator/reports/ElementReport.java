package de.tum.imomesa.integrator.reports;

import de.tum.imomesa.model.NamedElement;

public abstract class ElementReport<T extends NamedElement> extends AbstractReport<T> {

	public ElementReport(T element) {
		super(element, element.getName(), element.getDescription());
	}

}
