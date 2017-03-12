package de.tum.imomesa.integrator.printers.html;

import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.model.Workspace;

public class WorkspaceReportPrinter extends AbstractReportPrinter<Workspace, WorkspaceReport> {

	public WorkspaceReportPrinter(WorkspaceReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) {
		
	}

}
