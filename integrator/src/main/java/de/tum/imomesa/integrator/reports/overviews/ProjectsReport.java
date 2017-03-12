package de.tum.imomesa.integrator.reports.overviews;

import de.tum.imomesa.integrator.reports.OverviewReport;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.model.Workspace;

public class ProjectsReport extends OverviewReport<Workspace, ProjectReport> {

	public ProjectsReport(Workspace element) {
		super(element, "Projects");
	}

}
