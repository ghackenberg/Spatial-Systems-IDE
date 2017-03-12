package de.tum.imomesa.model.ports;

public class GenericEnergyPort extends EnergyPort {
	
	public GenericEnergyPort() {
		setReadType(Number.class);
		getWriteType().add(Number.class);
	}
	
}
