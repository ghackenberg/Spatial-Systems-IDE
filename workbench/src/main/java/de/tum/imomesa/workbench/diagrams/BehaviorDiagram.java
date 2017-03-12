package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.behaviors.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

public class BehaviorDiagram extends ExecutableDiagram<State, Transition, Behavior> {

	public BehaviorDiagram(Pane pane, Behavior executable, AbstractDiagramBehavior<Behavior> behavior,
			boolean interactive) {
		super(pane, executable, behavior, interactive);
	}

	@Override
	protected Paint getLabelFillInactive() {
		return Paints.GRADIENT_RED_ONE;
	}

	@Override
	protected Paint getLabelFillActive() {
		return Paints.GRADIENT_RED_TWO;
	}

	@Override
	protected void extend(Group group, Ellipse ellipse, State label, Tooltip tooltip) {
		// ignore
	}

	@Override
	protected StringExpression extend(Behavior executable) {
		return new ReadOnlyStringWrapper("");
	}

	@Override
	protected StringExpression extend(State label) {
		BooleanBinding hasParts = label.partsProperty().sizeProperty().greaterThan(0);

		return Bindings.when(hasParts).then(Bindings.concat(" ", label.partsProperty())).otherwise("");
	}

}
