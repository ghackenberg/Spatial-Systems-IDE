package de.tum.imomesa.integrator.reports;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.simulator.markers.SimulationMarker;

public abstract class OverviewReport<P extends NamedElement, C extends AbstractReport<?>> extends AbstractReport<P> {

	private List<C> reports = new ArrayList<>();
	
	public OverviewReport(P element, String name) {
		super(element, name, "");
	}
	
	public void addReport(C report) {
		reports.add(report);
	}
	
	public List<C> getReports() {
		return reports;
	}

	@Override
	public boolean getSuccessFlag() {
		for (C report : reports) {
			if (!report.getSuccessFlag()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getSuccessCount() {
		int result = 0;
		for (C report : reports) {
			result += report.getSuccessCount();
		}
		return result;
	}

	@Override
	public int getFailureCount() {
		int result = 0;
		for (C report : reports) {
			result += report.getFailureCount();
		}
		return result;
	}

	@Override
	public int getTimeoutCount() {
		int result = 0;
		for (C report : reports) {
			result += report.getTimeoutCount();
		}
		return result;
	}
	
	@Override
	public List<SimulationMarker> getMarkers() {
		List<SimulationMarker> result = new ArrayList<>();
		for (C report : reports) {
			result.addAll(report.getMarkers());
		}
		return result;
	}

}
