package de.tum.imomesa.integrator.printers.html;

import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.model.Project;

public class ProjectReportPrinter extends AbstractReportPrinter<Project, ProjectReport> {

	public ProjectReportPrinter(ProjectReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) {
		
	}

}
