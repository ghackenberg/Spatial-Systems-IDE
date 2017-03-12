package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;

public class MonitorDiagramBehavior extends ExecutableDiagramBehavior<Activity, Transition, Monitor> {

	@Override
	protected Transition createTransition() {
		return new Transition();
	}

}
