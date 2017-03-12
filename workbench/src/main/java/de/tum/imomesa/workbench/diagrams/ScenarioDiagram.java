package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.executables.scenarios.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

public class ScenarioDiagram extends ExecutableDiagram<Step, Transition, Scenario> {

	public ScenarioDiagram(Pane pane, Scenario executable, AbstractDiagramBehavior<Scenario> behavior,
			boolean interactive) {
		super(pane, executable, behavior, interactive);
	}

	@Override
	protected Paint getLabelFillInactive() {
		return Paints.GRADIENT_GREEN_ONE;
	}

	@Override
	protected Paint getLabelFillActive() {
		return Paints.GRADIENT_GREEN_TWO;
	}

	@Override
	protected void extend(Group group, Ellipse ellipse, Step label, Tooltip tooltip) {
		// Final
		Ellipse end = new Ellipse();

		bind(label, end, tooltip);

		end.radiusXProperty().bind(ellipse.radiusXProperty().subtract(MARKER_PADDING));
		end.radiusYProperty().bind(ellipse.radiusYProperty().subtract(MARKER_PADDING));
		end.setFill(null);
		end.setStroke(STROKE);
		end.setStrokeWidth(STROKE_WIDTH);
		end.visibleProperty().bind(getElement().finalLabelProperty().isEqualTo(label));

		group.getChildren().add(end);
	}

	@Override
	protected StringExpression extend(Scenario executable) {
		BooleanExpression hasPorts = executable.portsProperty().sizeProperty().greaterThan(0);

		return Bindings.when(hasPorts).then(Bindings.concat(" ", executable.portsProperty())).otherwise("");
	}

	@Override
	protected StringExpression extend(Step label) {
		return new ReadOnlyStringWrapper("");
	}

}
