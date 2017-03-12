package de.tum.imomesa.integrator.printers;

import java.io.IOException;
import java.io.Writer;

import de.tum.imomesa.integrator.reports.AbstractReport;
import de.tum.imomesa.model.NamedElement;

public abstract class AbstractReportPrinter<T extends NamedElement, R extends AbstractReport<T>> {

	private R report;
	
	public AbstractReportPrinter(R report) {
		this.report = report;
	}
	
	public R getReport() {
		return report;
	}
	
	public abstract void process(Writer writer) throws IOException;
	
}
