package de.tum.imomesa.utilities.visitors;

import de.tum.imomesa.model.Element;

public abstract class Visitor {
	
	public abstract <T extends Element> void dispatch(T object);

}
