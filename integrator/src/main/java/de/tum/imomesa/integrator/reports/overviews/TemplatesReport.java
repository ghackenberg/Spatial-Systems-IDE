package de.tum.imomesa.integrator.reports.overviews;

import de.tum.imomesa.integrator.reports.OverviewReport;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.model.Project;

public class TemplatesReport extends OverviewReport<Project, ComponentReport> {

	public TemplatesReport(Project element) {
		super(element, "Templates");
	}

}
