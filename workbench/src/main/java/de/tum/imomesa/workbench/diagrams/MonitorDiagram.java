package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

public class MonitorDiagram extends ExecutableDiagram<Activity, Transition, Monitor> {

	public MonitorDiagram(Pane pane, Monitor executable, AbstractDiagramBehavior<Monitor> behavior,
			boolean interactive) {
		super(pane, executable, behavior, interactive);
	}

	@Override
	protected Paint getLabelFillInactive() {
		return Paints.GRADIENT_BLUE_ONE;
	}

	@Override
	protected Paint getLabelFillActive() {
		return Paints.GRADIENT_BLUE_TWO;
	}

	@Override
	protected void extend(Group group, Ellipse ellipse, Activity label, Tooltip tooltip) {
		// ignore
	}

	@Override
	protected StringExpression extend(Monitor executable) {
		return new ReadOnlyStringWrapper("");
	}

	@Override
	protected StringExpression extend(Activity label) {
		return new ReadOnlyStringWrapper("");
	}

}
