package de.tum.imomesa.integrator.printers.html;

import java.io.Writer;

import de.tum.imomesa.integrator.printers.AbstractReportPrinter;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.model.executables.scenarios.Scenario;

public class ScenarioReportPrinter extends AbstractReportPrinter<Scenario, ScenarioReport> {

	public ScenarioReportPrinter(ScenarioReport report) {
		super(report);
	}

	@Override
	public void process(Writer writer) {
		
	}

}
