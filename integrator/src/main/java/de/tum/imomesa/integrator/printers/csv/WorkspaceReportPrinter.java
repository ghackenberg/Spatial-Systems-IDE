package de.tum.imomesa.integrator.printers.csv;

import java.io.IOException;
import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.model.Workspace;

public class WorkspaceReportPrinter extends AbstractReportPrinter<Workspace, WorkspaceReport> {

	public WorkspaceReportPrinter(WorkspaceReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) throws IOException {
		
		writer.write("Workspace;" + getReport().getElement() + "\n");	
		
		for (ProjectReport project : getReport().getProjectsReport().getReports()) {
			ProjectReportPrinter printer = new ProjectReportPrinter(project);
			printer.process(writer);
		}
	}

}
