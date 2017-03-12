package de.tum.imomesa.model.ports;


public class GenericPort extends DefinitionPort {
	
	public GenericPort() {
		setReadType(Boolean.class);
		getWriteType().add(Boolean.class);
	}
	
}
