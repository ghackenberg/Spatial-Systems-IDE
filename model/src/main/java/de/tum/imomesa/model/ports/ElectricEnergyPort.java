package de.tum.imomesa.model.ports;

public class ElectricEnergyPort extends EnergyPort {
	
	public ElectricEnergyPort() {
		setReadType(Number.class);
		getWriteType().add(Number.class);
	}
	
}
