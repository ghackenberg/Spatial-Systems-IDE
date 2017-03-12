package de.tum.imomesa.model.executables;

import de.tum.imomesa.model.Observation;

public class Variable extends Observation {

	public Variable() {
		this(Object.class);
	}

	public Variable(Class<?> typeObject) {
		setReadType(typeObject);
		getWriteType().add(typeObject);
	}
}
