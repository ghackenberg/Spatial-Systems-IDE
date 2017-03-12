package de.tum.imomesa.integrator.printers.html;

import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ComponentReportPrinter extends AbstractReportPrinter<DefinitionComponent, ComponentReport> {

	public ComponentReportPrinter(ComponentReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) {
		
	}

}
