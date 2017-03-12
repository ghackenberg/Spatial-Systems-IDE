package de.tum.imomesa.integrator.reports.overviews;

import de.tum.imomesa.integrator.reports.OverviewReport;
import de.tum.imomesa.integrator.reports.elements.ScenarioReport;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ScenariosReport extends OverviewReport<DefinitionComponent, ScenarioReport> {

	public ScenariosReport(DefinitionComponent element) {
		super(element, "Scenarios");
	}

}
