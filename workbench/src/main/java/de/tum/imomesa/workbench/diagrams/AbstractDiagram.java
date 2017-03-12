package de.tum.imomesa.workbench.diagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.commons.events.SelectionDiagramEvent;
import de.tum.imomesa.workbench.diagrams.listeners.EllipsePositionListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public abstract class AbstractDiagram<E extends Element> {

	// Stroke
	protected static final double STROKE_WIDTH = 1;
	protected static final Double[] STROKE_DASH = new Double[] { 5., 5. };
	protected static final Paint STROKE = Color.BLACK;

	// Shadow
	private static final double SHADOW_RADIUS = 10;
	private static final double SHADOW_OFFSET_X = 2.5;
	private static final double SHADOW_OFFSET_Y = 2.5;
	private static final Color SHADOW_COLOR = new Color(0, 0, 0, 0.75);
	protected static final DropShadow SHADOW = new DropShadow(SHADOW_RADIUS, SHADOW_OFFSET_X, SHADOW_OFFSET_Y,
			SHADOW_COLOR);

	// Container
	protected static final Paint CONTAINER_FILL_INACTIVE = new Color(1, 1, 1, 1. / 3.);
	protected static final Paint CONTAINER_FILL_ACTIVE = new Color(1, 1, 1, 1. / 4.);

	// Grid
	protected static final Paint GRID_STROKE = new Color(0, 0, 0, 1. / 4.);
	protected static final double GRID_STROKE_WIDTH = 1;

	// Edge
	protected static final double EDGE_STROKE_WIDTH = 1;
	protected static final Color EDGE_COLOR_INACTIVE = Color.BLACK;
	protected static final Color EDGE_COLOR_ACTIVE = Color.GOLD;
	protected static final double[] EDGE_ARROW_COORDINATES = new double[] { -10, -5, 0, 0, -10, 5 };

	// Dynamic

	private Pane pane;
	private E element;
	private AbstractDiagramBehavior<E> behavior;

	private Map<Node, Element> forward = new HashMap<>();
	private Map<Element, List<Node>> reverse = new HashMap<>();

	private ObjectProperty<Element> clickedElement = new SimpleObjectProperty<>();
	private ObjectProperty<Element> pressedElement = new SimpleObjectProperty<>();
	private ObjectProperty<Element> draggedElement = new SimpleObjectProperty<>();
	private ObjectProperty<Element> releasedElement = new SimpleObjectProperty<>();

	private ObjectProperty<Node> clickedNode = new SimpleObjectProperty<>();
	private ObjectProperty<Node> pressedNode = new SimpleObjectProperty<>();
	private ObjectProperty<Node> draggedNode = new SimpleObjectProperty<>();
	private ObjectProperty<Node> releasedNode = new SimpleObjectProperty<>();

	protected DoubleProperty unitSize = new SimpleDoubleProperty();
	protected DoubleProperty fontSize = new SimpleDoubleProperty();
	protected DoubleProperty portRadius = new SimpleDoubleProperty();
	protected DoubleProperty borderPadding = new SimpleDoubleProperty();
	protected DoubleProperty textPadding = new SimpleDoubleProperty();
	protected DoubleProperty cornerRadius = new SimpleDoubleProperty();
	protected DoubleProperty anchorRadius = new SimpleDoubleProperty();

	// TODO Remove interactive flag and move to abstract diagram behavior!
	private boolean interactive;

	public AbstractDiagram(Pane pane, E element, AbstractDiagramBehavior<E> behavior, boolean interactive) {
		this.pane = pane;
		this.element = element;
		this.behavior = behavior;

		unitSize.bind(Bindings.min(pane.widthProperty(), pane.heightProperty()).divide(100));
		fontSize.bind(unitSize.multiply(2));
		portRadius.bind(unitSize.multiply(1.25));
		borderPadding.bind(unitSize.multiply(5));
		textPadding.bind(unitSize.multiply(1));
		cornerRadius.bind(unitSize.multiply(2));
		anchorRadius.bind(portRadius.divide(4));

		pane.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));

		behavior.setDiagram(this);

		this.interactive = interactive;
	}

	public Pane getPane() {
		return pane;
	}

	public E getElement() {
		return element;
	}

	public AbstractDiagramBehavior<E> getBehavior() {
		return behavior;
	}

	// Clicked
	public Element getClickedElement() {
		return clickedElement.get();
	}

	public void setClickedElement(Element clicked) {
		this.clickedElement.set(clicked);
	}

	public ObjectProperty<Element> clickedElementProperty() {
		return clickedElement;
	}

	// Pressed
	public Element getPressedElement() {
		return pressedElement.get();
	}

	public void setPressedElement(Element pressed) {
		this.pressedElement.set(pressed);
	}

	public ObjectProperty<Element> pressedElementProperty() {
		return pressedElement;
	}

	// Dragged
	public Element getDraggedElement() {
		return draggedElement.get();
	}

	public void setDraggedElement(Element dragged) {
		this.draggedElement.set(dragged);
	}

	public ObjectProperty<Element> draggedElementProperty() {
		return draggedElement;
	}

	// Released
	public Element getReleasedElement() {
		return releasedElement.get();
	}

	public void setReleasedElement(Element released) {
		this.releasedElement.set(released);
	}

	public ObjectProperty<Element> releasedElementProperty() {
		return releasedElement;
	}

	// Clicked
	public Node getClickedNode() {
		return clickedNode.get();
	}

	public void setClickedNode(Node clicked) {
		this.clickedNode.set(clicked);
	}

	public ObjectProperty<Node> clickedNodeProperty() {
		return clickedNode;
	}

	// Pressed
	public Node getPressedNode() {
		return pressedNode.get();
	}

	public void setPressedNode(Node pressed) {
		this.pressedNode.set(pressed);
	}

	public ObjectProperty<Node> pressedNodeProperty() {
		return pressedNode;
	}

	// Dragged
	public Node getDraggedNode() {
		return draggedNode.get();
	}

	public void setDraggedNode(Node dragged) {
		this.draggedNode.set(dragged);
	}

	public ObjectProperty<Node> draggedNodeProperty() {
		return draggedNode;
	}

	// Released
	public Node getReleasedNode() {
		return releasedNode.get();
	}

	public void setReleasedNode(Node released) {
		this.releasedNode.set(released);
	}

	public ObjectProperty<Node> releasedNodeProperty() {
		return releasedNode;
	}

	// When active

	public When whenActive(Element element) {
		return Bindings.when(clickedElement.isEqualTo(element).or(pressedElement.isEqualTo(element))
				.or(draggedElement.isEqualTo(element)).or(releasedElement.isEqualTo(element)));
	}

	public When whenActive(Node node) {
		return Bindings.when(clickedNode.isEqualTo(node).or(pressedNode.isEqualTo(node)).or(draggedNode.isEqualTo(node))
				.or(releasedNode.isEqualTo(node)));
	}

	// Binding and resolving

	public Map<Node, Element> getForwardBindings() {
		return forward;
	}

	public Map<Element, List<Node>> getReverseBindings() {
		return reverse;
	}

	protected void unbind(Element element) {
		while (resolve(element) != null && resolve(element).size() > 0) {
			Node node = resolve(element).get(0);
			Group parent = (Group) node.getParent();
			parent.getChildren().remove(node);
			unbind(element, node);
		}
	}

	protected void unbind(Element element, Node node) {
		forward.remove(node);
		reverse.get(element).remove(node);
		if (reverse.get(element).size() == 0) {
			reverse.remove(element);
		}

		node.setOnMousePressed(null);
		node.setOnMouseDragged(null);
		node.setOnMouseReleased(null);
	}

	protected void bind(Element element, Node node, Tooltip tooltip) {
		forward.put(node, element);
		if (!reverse.containsKey(element)) {
			reverse.put(element, new ArrayList<>());
		}
		reverse.get(element).add(node);

		Tooltip.install(node, tooltip);

		if (interactive) {
			node.setCursor(Cursor.HAND);
			node.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setClickedElement(null);
					setClickedNode(null);

					setPressedElement(element);
					setPressedNode(node);

					event.setDragDetect(false);
					event.consume();

					behavior.handleMousePressed(event, element, node);
				}
			});
			node.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Node node = event.getPickResult().getIntersectedNode();
					Element element = resolve(node);

					setDraggedElement(element);
					setDraggedNode(node);

					event.setDragDetect(false);
					event.consume();

					behavior.handleMouseDragged(event, element, node);
				}
			});
			node.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setDraggedElement(null);
					setDraggedNode(null);

					Node node = event.getPickResult().getIntersectedNode();
					Element element = resolve(node);

					setReleasedElement(element);
					setReleasedNode(node);

					if (getPressedElement().equals(getReleasedElement())) {
						setClickedElement(element);
						setClickedNode(node);

						EventBus.getInstance().publish(new SelectionDiagramEvent(element));
					}

					event.consume();

					behavior.handleMouseReleased(event, element, node);

					setPressedElement(null);
					setPressedNode(null);

					setReleasedElement(null);
					setReleasedNode(null);
				}
			});
		}
	}

	public Element resolve(Node node) {
		return forward.get(node);
	}

	public List<Node> resolve(Element element) {
		return reverse.get(element);
	}

	public Node resolveOnly(Element element) {
		if (reverse.get(element).size() == 1) {
			return reverse.get(element).get(0);
		} else {
			throw new IllegalStateException("Exactly one node required!");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> resolve(Element element, Class<T> type) {
		List<T> result = new ArrayList<>();

		for (Node node : reverse.get(element)) {
			if (type.isAssignableFrom(node.getClass())) {
				result.add((T) node);
			}
		}

		return result;
	}

	public <T> T resolveOnly(Element element, Class<T> type) {
		List<T> intermediate = resolve(element, type);

		if (intermediate.size() == 1) {
			return intermediate.get(0);
		} else {
			throw new IllegalStateException("Exactly one node with respective type required!");
		}
	}

	// Tracking and converting

	protected List<Group> track(Group parent, Group child) {
		List<Group> result = new ArrayList<>();

		Group iterator = child;

		while (iterator != null && iterator != parent) {
			result.add(iterator);
			iterator = (Group) iterator.getParent();
		}

		// System.out.println(result);

		return result;
	}

	protected NumberExpression convertXTrack(List<Group> track) {
		return convertXTrack(track, 0);
	}

	private NumberExpression convertXTrack(List<Group> track, int index) {
		if (index == track.size()) {
			return new ReadOnlyDoubleWrapper(0);
		} else {
			return Bindings.add(track.get(index).translateXProperty(), convertXTrack(track, index + 1));
		}
	}

	protected NumberExpression convertYTrack(List<Group> track) {
		return convertYTrack(track, 0);
	}

	private NumberExpression convertYTrack(List<Group> track, int index) {
		if (index == track.size()) {
			return new ReadOnlyDoubleWrapper(0);
		} else {
			return Bindings.add(track.get(index).translateYProperty(), convertYTrack(track, index + 1));
		}
	}

	// Grid

	protected void addGrid(Group group, Rectangle rectangle, Element element, Tooltip tooltip) {
		// Grid
		for (int x = 1; x < Parameters.GRID_STEPS; x++) {
			Line line = new Line();

			bind(element, line, tooltip);

			line.startXProperty().bind(rectangle.widthProperty().divide(Parameters.GRID_STEPS).multiply(x));
			line.endXProperty().bind(rectangle.widthProperty().divide(Parameters.GRID_STEPS).multiply(x));
			line.endYProperty().bind(rectangle.heightProperty());
			line.setStroke(GRID_STROKE);
			line.setStrokeWidth(GRID_STROKE_WIDTH);

			group.getChildren().add(line);
		}
		for (int y = 1; y < Parameters.GRID_STEPS; y++) {
			Line line = new Line();

			bind(element, line, tooltip);

			line.startYProperty().bind(rectangle.heightProperty().divide(Parameters.GRID_STEPS).multiply(y));
			line.endXProperty().bind(rectangle.widthProperty());
			line.endYProperty().bind(rectangle.heightProperty().divide(Parameters.GRID_STEPS).multiply(y));
			line.setStroke(GRID_STROKE);
			line.setStrokeWidth(GRID_STROKE_WIDTH);

			group.getChildren().add(line);
		}
	}

	// Anchors

	protected void addAnchors(Group group, Rectangle rectangle, Element element, Tooltip tooltip) {
		for (int step = 1; step < Parameters.RECTANGLE_STEPS; step++) {
			// Circle
			Circle circle = new Circle();

			bind(element, circle, tooltip);

			circle.radiusProperty().bind(anchorRadius);
			circle.centerYProperty().bind(rectangle.heightProperty().divide(Parameters.RECTANGLE_STEPS).multiply(step));
			circle.visibleProperty().bind(draggedElement.isNotNull().or(draggedNode.isNotNull()));

			group.getChildren().add(circle);
		}
		for (int step = 1; step < Parameters.RECTANGLE_STEPS; step++) {
			// Circle
			Circle circle = new Circle();

			bind(element, circle, tooltip);

			circle.radiusProperty().bind(anchorRadius);
			circle.centerXProperty().bind(rectangle.widthProperty().divide(Parameters.RECTANGLE_STEPS).multiply(step));
			circle.centerYProperty().bind(rectangle.heightProperty());
			circle.visibleProperty().bind(draggedElement.isNotNull().or(draggedNode.isNotNull()));

			group.getChildren().add(circle);
		}
		for (int step = 1; step < Parameters.RECTANGLE_STEPS; step++) {
			// Circle
			Circle circle = new Circle();

			bind(element, circle, tooltip);

			circle.radiusProperty().bind(anchorRadius);
			circle.centerXProperty().bind(rectangle.widthProperty());
			circle.centerYProperty().bind(rectangle.heightProperty().divide(Parameters.RECTANGLE_STEPS).multiply(step));
			circle.visibleProperty().bind(draggedElement.isNotNull().or(draggedNode.isNotNull()));

			group.getChildren().add(circle);
		}
		for (int step = 1; step < Parameters.RECTANGLE_STEPS; step++) {
			// Circle
			Circle circle = new Circle();

			bind(element, circle, tooltip);

			circle.radiusProperty().bind(anchorRadius);
			circle.centerXProperty().bind(rectangle.widthProperty().divide(Parameters.RECTANGLE_STEPS).multiply(step));
			circle.visibleProperty().bind(draggedElement.isNotNull().or(draggedNode.isNotNull()));

			group.getChildren().add(circle);
		}
	}

	protected void addAnchors(Group group, Ellipse ellipse, Element element, Tooltip tooltip) {
		for (int step = 0; step < Parameters.ELLIPSE_STEPS; step++) {
			// Internal group
			Group internalGroup = new Group();

			bind(element, internalGroup, tooltip);

			DoubleProperty angle = new ReadOnlyDoubleWrapper(360. / Parameters.ELLIPSE_STEPS * step);

			EllipsePositionListener listener = new EllipsePositionListener(ellipse, internalGroup, angle);

			ellipse.radiusXProperty().addListener(listener);
			ellipse.radiusYProperty().addListener(listener);

			group.getChildren().add(internalGroup);

			// Circle
			Circle circle = new Circle();

			bind(element, circle, tooltip);

			circle.radiusProperty().bind(anchorRadius);
			circle.visibleProperty().bind(draggedElement.isNotNull().or(draggedNode.isNotNull()));

			internalGroup.getChildren().add(circle);
		}
	}

}
