package de.tum.imomesa.integrator.reports.elements;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.integrator.reports.ElementReport;
import de.tum.imomesa.integrator.reports.overviews.ProjectsReport;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public class WorkspaceReport extends ElementReport<Workspace> {

	private ProjectsReport projects;

	public WorkspaceReport(Workspace element) {
		super(element);

		projects = new ProjectsReport(element);
	}

	public ProjectsReport getProjectsReport() {
		return projects;
	}

	@Override
	public boolean getSuccessFlag() {
		return projects.getSuccessFlag();
	}

	@Override
	public int getSuccessCount() {
		return projects.getSuccessCount();
	}

	@Override
	public int getFailureCount() {
		return projects.getFailureCount();
	}

	@Override
	public int getTimeoutCount() {
		return projects.getTimeoutCount();
	}

	@Override
	public List<SimulationMarker> getMarkers() {
		return new ArrayList<>(projects.getMarkers());
	}

}
