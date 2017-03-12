package de.tum.imomesa.integrator.printers.csv;

import java.io.IOException;
import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.integrator.reports.elements.ProjectReport;
import de.tum.imomesa.model.Project;

public class ProjectReportPrinter extends AbstractReportPrinter<Project, ProjectReport> {

	public ProjectReportPrinter(ProjectReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) throws IOException {
		
		writer.write("Project;" + getReport().getElement() + "\n");
		
		for (ComponentReport component : getReport().getTemplatesReport().getReports()) {
			ComponentReportPrinter printer = new ComponentReportPrinter(component);
			printer.process(writer);
		}
		
	}

}
