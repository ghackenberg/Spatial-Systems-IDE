package de.tum.imomesa.integrator.printers.csv;

import java.io.IOException;
import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.model.executables.scenarios.Scenario;

public class ScenarioReportPrinter extends AbstractReportPrinter<Scenario, ScenarioReport> {

	public ScenarioReportPrinter(ScenarioReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) throws IOException {
		
		writer.write("Scenario;" + getReport().getElement() + ";" + getReport().getMarkers() + "\n");
		
	}

}
