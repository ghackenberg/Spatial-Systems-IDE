package de.tum.imomesa.integrator.reports.overviews;

import de.tum.imomesa.integrator.reports.OverviewReport;
import de.tum.imomesa.integrator.reports.elements.ComponentReport;
import de.tum.imomesa.model.components.DefinitionComponent;

public class ComponentsReport extends OverviewReport<DefinitionComponent, ComponentReport> {
	
	public ComponentsReport(DefinitionComponent element) {
		super(element, "Components");
	}

}
