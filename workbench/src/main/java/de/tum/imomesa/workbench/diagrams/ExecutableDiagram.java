package de.tum.imomesa.workbench.diagrams;

import java.util.List;

import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.workbench.diagrams.listeners.EllipsePositionListener;
import de.tum.imomesa.workbench.diagrams.listeners.LineAngleListener;
import de.tum.imomesa.workbench.diagrams.listeners.PortPositionListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.collections.ListChangeListener;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

public abstract class ExecutableDiagram<L extends Label, T extends Transition<L>, E extends Executable<L, T>>
		extends AbstractDiagram<E> {

	protected static final double MARKER_PADDING = 5;

	protected static final double TRANSITION_TEXT_WIDTH = 100;

	private static final Paint TRANSITION_SOURCE_FILL_INACTIVE = Paints.GRAY_ONE;
	private static final Paint TRANSITION_SOURCE_FILL_ACTIVE = Paints.GRAY_TWO;

	private static final Paint TRANSITION_TARGET_FILL_INACTIVE = Paints.GRAY_THREE;
	private static final Paint TRANSITION_TARGET_FILL_ACTIVE = Paints.GRAY_FOUR;

	public ExecutableDiagram(Pane pane, E executable, AbstractDiagramBehavior<E> behavior, boolean interactive) {
		super(pane, executable, behavior, interactive);

		transform(pane, executable);
	}

	protected abstract Paint getLabelFillInactive();

	protected abstract Paint getLabelFillActive();

	public void transform(Pane pane, E executable) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty()
				.bind(Bindings.concat(executable.nameProperty(), " : ", executable.initialLabelProperty()));

		// Group
		Group group = new Group();

		bind(executable, group, tooltip);

		group.translateXProperty().bind(borderPadding);
		group.translateYProperty().bind(borderPadding);

		pane.getChildren().add(group);

		// Boundary
		Rectangle rectangle = new Rectangle();

		bind(executable, rectangle, tooltip);

		rectangle.widthProperty().bind(pane.widthProperty().subtract(borderPadding.multiply(2)));
		rectangle.heightProperty().bind(pane.heightProperty().subtract(borderPadding.multiply(2)));
		rectangle.fillProperty()
				.bind(whenActive(executable).then(CONTAINER_FILL_ACTIVE).otherwise(CONTAINER_FILL_INACTIVE));
		rectangle.arcWidthProperty().bind(cornerRadius);
		rectangle.arcHeightProperty().bind(cornerRadius);
		rectangle.setStroke(STROKE);
		rectangle.setStrokeWidth(STROKE_WIDTH);
		rectangle.setEffect(SHADOW);

		group.getChildren().add(rectangle);

		// Grid
		addGrid(group, rectangle, executable, tooltip);

		// Name
		Text text = new Text();

		bind(executable, text, tooltip);

		BooleanBinding hasVariables = executable.variablesProperty().sizeProperty().greaterThan(0);

		StringBinding variables = Bindings.when(hasVariables).then(Bindings.concat(" ", executable.variablesProperty()))
				.otherwise("");

		text.textProperty().bind(Bindings.concat("«" + executable.getClass().getSimpleName() + "» ",
				executable.nameProperty(), variables, extend(executable)));
		text.xProperty().bind(textPadding);
		text.yProperty().bind(textPadding);
		text.wrappingWidthProperty().bind(rectangle.widthProperty().subtract(textPadding.multiply(2)));
		text.setTextOrigin(VPos.TOP);

		group.getChildren().add(text);

		// Labels
		for (L label : executable.getLabels()) {
			transform(group, rectangle, label);
		}
		executable.labelsProperty().addListener(new ListChangeListener<L>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends L> change) {
				while (change.next()) {
					for (L removed : change.getRemoved()) {
						unbind(removed);
					}
					for (L added : change.getAddedSubList()) {
						transform(group, rectangle, added);
					}
				}
			}
		});

		// Transitions
		for (T transition : executable.getTransitions()) {
			transform(group, rectangle, transition);
		}
		executable.transitionsProperty().addListener(new ListChangeListener<T>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> change) {
				while (change.next()) {
					for (T removed : change.getRemoved()) {
						unbind(removed);
					}
					for (T added : change.getAddedSubList()) {
						transform(group, rectangle, added);
					}
				}
			}
		});
	}

	protected abstract StringExpression extend(E executable);

	public void transform(Group parent, Rectangle bounds, L label) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Bindings.concat(label.nameProperty(), " : ", label.actionsProperty(), " / ",
				label.propertiesProperty()));

		// Group
		Group group = new Group();

		bind(label, group, tooltip);

		group.translateXProperty()
				.bind(Bindings.add(bounds.xProperty(), Bindings.multiply(bounds.widthProperty(), label.xProperty())));
		group.translateYProperty()
				.bind(Bindings.add(bounds.yProperty(), Bindings.multiply(bounds.heightProperty(), label.yProperty())));

		parent.getChildren().add(group);

		// Boundary
		Ellipse ellipse = new Ellipse();

		bind(label, ellipse, tooltip);

		ellipse.radiusXProperty()
				.bind(Bindings.multiply(bounds.widthProperty(), Bindings.divide(label.widthProperty(), 2)));
		ellipse.radiusYProperty()
				.bind(Bindings.multiply(bounds.heightProperty(), Bindings.divide(label.heightProperty(), 2)));
		ellipse.fillProperty().bind(whenActive(label).then(getLabelFillActive()).otherwise(getLabelFillInactive()));
		ellipse.setStroke(STROKE);
		ellipse.setStrokeWidth(STROKE_WIDTH);
		ellipse.setEffect(SHADOW);

		group.getChildren().add(ellipse);

		// Initial
		Ellipse initial = new Ellipse();

		bind(label, initial, tooltip);

		initial.radiusXProperty().bind(ellipse.radiusXProperty().subtract(MARKER_PADDING));
		initial.radiusYProperty().bind(ellipse.radiusYProperty().subtract(MARKER_PADDING));
		initial.setFill(null);
		initial.setStroke(STROKE);
		initial.setStrokeWidth(STROKE_WIDTH);
		initial.getStrokeDashArray().addAll(STROKE_DASH);
		initial.visibleProperty().bind(getElement().initialLabelProperty().isEqualTo(label));

		group.getChildren().add(initial);

		// Final
		extend(group, ellipse, label, tooltip);

		// Anchors
		addAnchors(group, ellipse, label, tooltip);

		// Text
		Text text = new Text();

		bind(label, text, tooltip);

		/*
		 * BooleanBinding hasActions =
		 * label.actionsProperty().sizeProperty().greaterThan(0);
		 */
		BooleanBinding hasProperties = label.propertiesProperty().sizeProperty().greaterThan(0);

		/*
		 * StringBinding actions =
		 * Bindings.when(hasActions).then(Bindings.concat(" ",
		 * label.actionsProperty())) .otherwise("");
		 */
		StringBinding properties = Bindings.when(hasProperties).then(Bindings.concat(" ", label.propertiesProperty()))
				.otherwise("");

		text.textProperty()
				.bind(Bindings.concat(
						/* "«" + label.getClass().getSimpleName() + "»\n", */label.nameProperty()/* , actions */,
						properties, extend(label)));
		text.xProperty().bind(Bindings.subtract(0, ellipse.radiusXProperty()).add(textPadding));
		text.wrappingWidthProperty()
				.bind(Bindings.multiply(ellipse.radiusXProperty(), 2).subtract(textPadding.multiply(2)));
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);

		group.getChildren().add(text);
	}

	protected abstract void extend(Group group, Ellipse ellipse, L label, Tooltip tooltip);

	protected abstract StringExpression extend(L label);

	public void transform(Group parent, Rectangle bounds, T transition) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Bindings.concat(transition.nameProperty(), " : ", transition.guardProperty(), " / ",
				transition.actionsProperty()));

		// Group
		Group group = new Group();

		bind(transition, group, tooltip);

		parent.getChildren().add(group);

		// Source port group
		Group sourcePortGroup = new Group();

		bind(transition, sourcePortGroup, tooltip);

		Label sourceLabel = transition.getSourceLabel();
		Ellipse sourceEllipse = resolve(sourceLabel, Ellipse.class).get(0);
		Group sourceGroup = resolve(sourceLabel, Group.class).get(0);

		EllipsePositionListener sourceListener = new EllipsePositionListener(sourceEllipse, sourcePortGroup,
				transition.sourceAngleProperty());

		transition.sourceAngleProperty().addListener(sourceListener);

		sourceEllipse.centerXProperty().addListener(sourceListener);
		sourceEllipse.centerYProperty().addListener(sourceListener);
		sourceEllipse.radiusXProperty().addListener(sourceListener);
		sourceEllipse.radiusYProperty().addListener(sourceListener);

		sourceGroup.getChildren().add(sourcePortGroup);

		// Source port
		Circle sourcePort = new Circle();

		bind(transition, sourcePort, tooltip);

		sourcePort.radiusProperty().bind(portRadius);
		sourcePort.fillProperty().bind(
				whenActive(sourcePort).then(TRANSITION_SOURCE_FILL_ACTIVE).otherwise(TRANSITION_SOURCE_FILL_INACTIVE));
		sourcePort.setStroke(STROKE);
		sourcePort.setStrokeWidth(STROKE_WIDTH);

		sourcePortGroup.getChildren().add(sourcePort);

		// Target port group
		Group targetPortGroup = new Group();

		bind(transition, targetPortGroup, tooltip);

		L target = transition.getTargetLabel();
		Ellipse targetEllipse = resolve(target, Ellipse.class).get(0);
		Group targetGroup = resolve(target, Group.class).get(0);

		EllipsePositionListener targetListener = new EllipsePositionListener(targetEllipse, targetPortGroup,
				transition.targetAngleProperty());

		transition.targetAngleProperty().addListener(targetListener);

		targetEllipse.centerXProperty().addListener(targetListener);
		targetEllipse.centerYProperty().addListener(targetListener);
		targetEllipse.radiusXProperty().addListener(targetListener);
		targetEllipse.radiusYProperty().addListener(targetListener);

		targetGroup.getChildren().add(targetPortGroup);

		// Target port
		Circle targetPort = new Circle();

		bind(transition, targetPort, tooltip);

		targetPort.radiusProperty().bind(portRadius);
		targetPort.fillProperty().bind(
				whenActive(targetPort).then(TRANSITION_TARGET_FILL_ACTIVE).otherwise(TRANSITION_TARGET_FILL_INACTIVE));
		targetPort.setStroke(STROKE);
		targetPort.setStrokeWidth(STROKE_WIDTH);

		targetPortGroup.getChildren().add(targetPort);

		// Line
		Line line = new Line();

		bind(transition, line, tooltip);

		List<Group> sourcePortTrack = track(parent, sourcePortGroup);
		List<Group> targetPortTrack = track(parent, targetPortGroup);

		NumberExpression sourcePortX = convertXTrack(sourcePortTrack);
		NumberExpression sourcePortY = convertYTrack(sourcePortTrack);
		NumberExpression targetPortX = convertXTrack(targetPortTrack);
		NumberExpression targetPortY = convertYTrack(targetPortTrack);

		PortPositionListener positionListener = new PortPositionListener(sourcePortX, sourcePortY, targetPortX,
				targetPortY, portRadius.multiply(1.5), line);

		sourcePortX.addListener(positionListener);
		sourcePortY.addListener(positionListener);
		targetPortX.addListener(positionListener);
		targetPortY.addListener(positionListener);

		portRadius.addListener(positionListener);

		line.strokeProperty().bind(whenActive(transition).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		line.setStrokeWidth(EDGE_STROKE_WIDTH);

		group.getChildren().add(line);

		// Polygon
		Polygon polygon = new Polygon(EDGE_ARROW_COORDINATES);

		bind(transition, polygon, tooltip);

		Rotate rotate = new Rotate();

		polygon.translateXProperty().bind(line.endXProperty());
		polygon.translateYProperty().bind(line.endYProperty());
		polygon.getTransforms().add(rotate);
		polygon.fillProperty().bind(whenActive(transition).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		polygon.strokeProperty().bind(whenActive(transition).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		polygon.setStrokeWidth(STROKE_WIDTH);

		group.getChildren().add(polygon);

		LineAngleListener listener = new LineAngleListener(line, rotate.angleProperty());

		line.startXProperty().addListener(listener);
		line.startYProperty().addListener(listener);
		line.endXProperty().addListener(listener);
		line.endYProperty().addListener(listener);

		// Text
		Text text = new Text();

		bind(transition, text, tooltip);

		/*
		 * BooleanBinding hasActions =
		 * transition.actionsProperty().sizeProperty().greaterThan(0);
		 */

		/*
		 * StringBinding actions =
		 * Bindings.when(hasActions).then(Bindings.concat(" ",
		 * transition.actionsProperty())) .otherwise("");
		 */

		text.textProperty().bind(Bindings.concat(
				/* "«" + transition.getClass().getSimpleName() + "»\n", */transition
						.nameProperty()/* , actions */));
		text.xProperty().bind(line.startXProperty().add(line.endXProperty()).divide(2).subtract(TRANSITION_TEXT_WIDTH));
		text.yProperty().bind(line.startYProperty().add(line.endYProperty()).divide(2));
		text.setWrappingWidth(TRANSITION_TEXT_WIDTH * 2);
		text.fillProperty().bind(whenActive(transition).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);

		group.getChildren().add(text);
	}

}
