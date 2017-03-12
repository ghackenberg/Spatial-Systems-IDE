package de.tum.imomesa.integrator.reports.elements;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.integrator.reports.ElementReport;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.SimulationMarker;
import de.tum.imomesa.simulator.markers.TimeoutMarker;

public class ScenarioReport extends ElementReport<Scenario> {

	private List<SimulationMarker> markers = new ArrayList<>();

	public ScenarioReport(Scenario element) {
		super(element);
	}

	@Override
	public boolean getSuccessFlag() {
		for (SimulationMarker marker : markers) {
			if (marker instanceof ErrorMarker) {
				return false;
			}
			if (marker instanceof TimeoutMarker) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getSuccessCount() {
		for (SimulationMarker marker : markers) {
			if (marker instanceof ErrorMarker) {
				return 0;
			}
			if (marker instanceof TimeoutMarker) {
				return 0;
			}
		}
		return 1;
	}

	@Override
	public int getFailureCount() {
		for (SimulationMarker marker : markers) {
			if (marker instanceof ErrorMarker) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int getTimeoutCount() {
		for (SimulationMarker marker : markers) {
			if (marker instanceof TimeoutMarker) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public List<SimulationMarker> getMarkers() {
		return markers;
	}

}
