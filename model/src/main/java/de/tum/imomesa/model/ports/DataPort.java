package de.tum.imomesa.model.ports;

public class DataPort extends DefinitionPort {
	
	public DataPort() {
		setReadType(Boolean.class);
		getWriteType().add(Boolean.class);
	}
	
}
