package de.tum.imomesa.analyzer.descriptors;

import de.tum.imomesa.model.executables.scenarios.Scenario;

public class SimulationDescriptor {

	private Scenario scenario;
	private String result;
	private int count;

	public SimulationDescriptor(Scenario scenario, String result) {
		this.scenario = scenario;
		this.result = result;

		count = 1;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public String getResult() {
		return result;
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() {
		count++;
	}

}
