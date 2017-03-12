package de.tum.imomesa.integrator;

import java.io.File;
import java.io.FileWriter;

import de.tum.imomesa.integrator.reports.elements.WorkspaceReport;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.utilities.managers.StorageManager;

public class Main {

	private static long MAXHOURS = 0;
	private static long MAXMINUTES = 0;
	private static long MAXSECONDS = 60;
	private static long MAXTIME = 1000 * (MAXSECONDS + 60 * (MAXMINUTES + 60 * MAXHOURS));

	public static void main(String[] args) {
		try {
			// Create instance
			StorageManager.createInstance(new File(args[0]));
			// Obtain workspace
			Workspace workspace = StorageManager.getInstance().getWorkspace();
			// Create report
			WorkspaceReport report = new Integrator(MAXTIME).process(workspace);
			// Create writers
			FileWriter csv = new FileWriter(System.currentTimeMillis() + "_report.csv");
			FileWriter html = new FileWriter(System.currentTimeMillis() + "_report.html");
			// Process printers
			new de.tum.imomesa.integrator.printers.csv.WorkspaceReportPrinter(report).process(csv);
			new de.tum.imomesa.integrator.printers.html.WorkspaceReportPrinter(report).process(html);
			// Close writers
			csv.close();
			html.close();
		}
		catch (Exception e) {
			// Report exception
			e.printStackTrace();
		}
		finally {
			// Close connection
			StorageManager.getInstance().close();
		}
	}
	
}
