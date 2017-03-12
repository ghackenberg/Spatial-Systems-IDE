package de.tum.imomesa.integrator.reports.elements;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.integrator.reports.ElementReport;
import de.tum.imomesa.integrator.reports.overviews.ComponentsReport;
import de.tum.imomesa.integrator.reports.overviews.ScenariosReport;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class ComponentReport extends ElementReport<DefinitionComponent> {
	
	private ComponentsReport components;
	private ScenariosReport scenarios;

	public ComponentReport(DefinitionComponent element) {
		super(element);
		
		components = new ComponentsReport(element);
		scenarios = new ScenariosReport(element);
	}
	
	public ComponentsReport getComponentsReport() {
		return components;
	}
	
	public ScenariosReport getScenariosReport() {
		return scenarios;
	}

	@Override
	public boolean getSuccessFlag() {
		return components.getSuccessFlag() && scenarios.getSuccessFlag();
	}

	@Override
	public int getSuccessCount() {
		return components.getSuccessCount() + scenarios.getSuccessCount();
	}

	@Override
	public int getFailureCount() {
		return components.getFailureCount() + scenarios.getFailureCount();
	}

	@Override
	public int getTimeoutCount() {
		return components.getTimeoutCount() + scenarios.getTimeoutCount();
	}
	
	@Override
	public List<SimulationMarker> getMarkers() {
		List<SimulationMarker> result = new ArrayList<>();
		result.addAll(components.getMarkers());
		result.addAll(scenarios.getMarkers());
		return result;
	}

}
