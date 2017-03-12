package de.tum.imomesa.integrator.reports.elements;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.integrator.reports.ElementReport;
import de.tum.imomesa.integrator.reports.overviews.TemplatesReport;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class ProjectReport extends ElementReport<Project> {
	
	private TemplatesReport templates;
	private ComponentReport component;

	public ProjectReport(Project element) {
		super(element);
		
		templates = new TemplatesReport(element);
	}
	
	public TemplatesReport getTemplatesReport() {
		return templates;
	}
	
	public void setComponentReport(ComponentReport report) {
		component = report;
	}
	
	public ComponentReport getComponentReport() {
		return component;
	}

	@Override
	public boolean getSuccessFlag() {
		return templates.getSuccessFlag() && component.getSuccessFlag();
	}

	@Override
	public int getSuccessCount() {
		return templates.getSuccessCount() + component.getSuccessCount();
	}

	@Override
	public int getFailureCount() {
		return templates.getFailureCount() + component.getFailureCount();
	}

	@Override
	public int getTimeoutCount() {
		return templates.getTimeoutCount() + component.getTimeoutCount();
	}
	
	@Override
	public List<SimulationMarker> getMarkers() {
		List<SimulationMarker> result = new ArrayList<>();
		result.addAll(templates.getMarkers());
		result.addAll(component.getMarkers());
		return result;
	}

}
