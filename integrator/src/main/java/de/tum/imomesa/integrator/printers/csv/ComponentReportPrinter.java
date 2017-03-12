package de.tum.imomesa.integrator.printers.csv;

import java.io.IOException;
import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ComponentReportPrinter extends AbstractReportPrinter<DefinitionComponent, ComponentReport> {

	public ComponentReportPrinter(ComponentReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) throws IOException {
		
		writer.write("Component;" + getReport().getElement() + "\n");
		
		for (ScenarioReport scenario : getReport().getScenariosReport().getReports()) {
			ScenarioReportPrinter printer = new ScenarioReportPrinter(scenario);
			printer.process(writer);
		}
		for (ComponentReport component : getReport().getComponentsReport().getReports()) {
			ComponentReportPrinter printer = new ComponentReportPrinter(component);
			printer.process(writer);
		}
		
	}

}
