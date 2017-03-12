package de.tum.imomesa.workbench.diagrams;

import java.util.List;

import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.DataPort;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.EnergyPort;
import de.tum.imomesa.model.ports.GenericPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.workbench.diagrams.listeners.LineAngleListener;
import de.tum.imomesa.workbench.diagrams.listeners.PortPositionListener;
import de.tum.imomesa.workbench.diagrams.listeners.RectanglePositionListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberExpression;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

public class ComponentDiagram extends AbstractDiagram<DefinitionComponent> {

	private static final double CHANNEL_TEXT_WIDTH = 100;

	private static final Paint COMPONENT_FILL_INACTIVE = Paints.GRADIENT_GRAY_ONE;
	private static final Paint COMPONENT_FILL_ACTIVE = Paints.GRADIENT_GRAY_TWO;

	private static final Paint SLOT_FILL_INACTIVE = Paints.GRADIENT_RED_ONE;
	private static final Paint SLOT_FILL_ACTIVE = Paints.GRADIENT_RED_TWO;

	private static final Color MATERIAL_INPUT_COLOR_INACTIVE = Paints.RED_ONE;
	private static final Color MATERIAL_INPUT_COLOR_ACTIVE = Paints.RED_TWO;
	private static final Color MATERIAL_OUTPUT_COLOR_INACTIVE = Paints.RED_THREE;
	private static final Color MATERIAL_OUTPUT_COLOR_ACTIVE = Paints.RED_FOUR;

	private static final Color ENERGY_INPUT_COLOR_INACTIVE = Paints.GREEN_ONE;
	private static final Color ENERGY_INPUT_COLOR_ACTIVE = Paints.GREEN_TWO;
	private static final Color ENERGY_OUTPUT_COLOR_INACTIVE = Paints.GREEN_THREE;
	private static final Color ENERGY_OUTPUT_COLOR_ACTIVE = Paints.GREEN_FOUR;

	private static final Color DATA_INPUT_COLOR_INACTIVE = Paints.BLUE_ONE;
	private static final Color DATA_INPUT_COLOR_ACTIVE = Paints.BLUE_TWO;
	private static final Color DATA_OUTPUT_COLOR_INACTIVE = Paints.BLUE_THREE;
	private static final Color DATA_OUTPUT_COLOR_ACTIVE = Paints.BLUE_FOUR;

	private static final Color GENERIC_INPUT_COLOR_INACTIVE = Paints.GRAY_ONE;
	private static final Color GENERIC_INPUT_COLOR_ACTIVE = Paints.GRAY_TWO;
	private static final Color GENERIC_OUTPUT_COLOR_INACTIVE = Paints.GRAY_THREE;
	private static final Color GENERIC_OUTPUT_COLOR_ACTIVE = Paints.GRAY_FOUR;

	public ComponentDiagram(Pane pane, DefinitionComponent component,
			AbstractDiagramBehavior<DefinitionComponent> behavior, boolean interactive) {
		super(pane, component, behavior, interactive);

		transform(pane, component);
	}

	public void transform(Pane parent, DefinitionComponent component) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(component.nameProperty());

		// Group
		Group group = new Group();

		parent.getChildren().add(group);

		bind(component, group, tooltip);

		group.translateXProperty().bind(borderPadding);
		group.translateYProperty().bind(borderPadding);

		// Boundary
		Rectangle rectangle = new Rectangle();

		bind(component, rectangle, tooltip);

		rectangle.widthProperty().bind(parent.widthProperty().subtract(borderPadding.multiply(2)));
		rectangle.heightProperty().bind(parent.heightProperty().subtract(borderPadding.multiply(2)));
		rectangle.fillProperty()
				.bind(whenActive(component).then(CONTAINER_FILL_ACTIVE).otherwise(CONTAINER_FILL_INACTIVE));
		rectangle.arcWidthProperty().bind(cornerRadius);
		rectangle.arcHeightProperty().bind(cornerRadius);
		rectangle.setStroke(STROKE);
		rectangle.setStrokeWidth(STROKE_WIDTH);
		rectangle.setEffect(SHADOW);

		group.getChildren().add(rectangle);

		// Grid
		addGrid(group, rectangle, component, tooltip);

		// Anchors
		addAnchors(group, rectangle, component, tooltip);

		// Name
		Text text = new Text();

		bind(component, text, tooltip);

		text.textProperty()
				.bind(Bindings.concat("«" + component.getClass().getSimpleName() + "» ", component.nameProperty()));
		text.xProperty().bind(textPadding);
		text.yProperty().bind(textPadding);
		text.setTextOrigin(VPos.TOP);

		group.getChildren().add(text);

		// Ports
		for (DefinitionPort port : component.getPorts()) {

			// TODO Remove fix!
			for (StaticChannel channel : port.getIncomingStaticChannels()) {
				if (channel.getSource().getPath().contains(component)) {
					if (!component.getChannels().contains(channel)) {
						component.getChannels().add(channel);
					}
				}
			}
			for (StaticChannel channel : port.getOutgoingStaticChannels()) {
				if (channel.getTarget().getPath().contains(component)) {
					if (!component.getChannels().contains(channel)) {
						component.getChannels().add(channel);
					}
				}
			}

			if (!(port instanceof InteractionMaterialPort)) {
				transform(group, rectangle, port);
			} else {
				transform(group, rectangle, (InteractionMaterialPort) port);
			}
		}
		component.portsProperty().addListener(new ListChangeListener<DefinitionPort>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends DefinitionPort> change) {
				while (change.next()) {
					for (DefinitionPort removed : change.getRemoved()) {
						if (!(removed instanceof InteractionMaterialPort)) {
							unbind(removed);
						} else {
							for (DefinitionPort port : ((InteractionMaterialPort) removed).getPorts()) {
								if (show(port)) {
									unbind(port);
								}
							}
							unbind(removed);
						}
					}
					for (DefinitionPort added : change.getAddedSubList()) {
						if (!(added instanceof InteractionMaterialPort)) {
							transform(group, rectangle, added);
						} else {
							transform(group, rectangle, (InteractionMaterialPort) added);
						}
					}
				}
			}
		});

		// Components
		for (Component<?> child : component.getComponents()) {

			// TODO Remove fix!
			for (Port port : child.getPorts()) {
				for (StaticChannel channel : port.getIncomingStaticChannels()) {
					if (!channel.getSource().getPath().contains(child)) {
						if (!component.getChannels().contains(channel)) {
							component.getChannels().add(channel);
						}
					}
				}
				for (StaticChannel channel : port.getOutgoingStaticChannels()) {
					if (!channel.getTarget().getPath().contains(child)) {
						if (!component.getChannels().contains(channel)) {
							component.getChannels().add(channel);
						}
					}
				}
			}

			transform(group, rectangle, child);
		}
		component.componentsProperty().addListener(new ListChangeListener<Component<?>>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Component<?>> change) {
				while (change.next()) {
					for (Component<?> removed : change.getRemoved()) {
						for (Port port : removed.getPorts()) {
							if (show(port)) {
								unbind(port);
							}
						}
						unbind(removed);
					}
					for (Component<?> added : change.getAddedSubList()) {
						transform(group, rectangle, added);
					}
				}
			}
		});

		// Channels
		for (StaticChannel channel : component.getChannels()) {
			transform(group, rectangle, channel);
		}
		component.channelsProperty().addListener(new ListChangeListener<StaticChannel>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends StaticChannel> change) {
				while (change.next()) {
					for (StaticChannel removed : change.getRemoved()) {
						unbind(removed);
					}
					for (StaticChannel added : change.getAddedSubList()) {
						transform(group, rectangle, added);
					}
				}
			}
		});
	}

	public void transform(Group parent, Rectangle bounds, Port port) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(
				Bindings.concat(port.nameProperty(), " : ", port.writeTypeProperty(), " / ", port.readTypeProperty()));

		// Port
		Port colorPort;

		if (port instanceof ReferencePort) {
			colorPort = ((ReferencePort) port).getPortImplementation();
		} else {
			colorPort = port;
		}

		// Group
		Group group = new Group();

		parent.getChildren().add(group);

		bind(port, group, tooltip);

		RectanglePositionListener listener = new RectanglePositionListener(bounds, group, port.angleProperty());

		port.angleProperty().addListener(listener);

		bounds.xProperty().addListener(listener);
		bounds.yProperty().addListener(listener);
		bounds.widthProperty().addListener(listener);
		bounds.heightProperty().addListener(listener);

		// Circle
		Circle circle = new Circle();

		bind(port, circle, tooltip);

		if (colorPort instanceof MaterialPort) {
			if (colorPort.getDirection().equals(Direction.INPUT.ordinal())) {
				circle.fillProperty().bind(
						whenActive(port).then(MATERIAL_INPUT_COLOR_ACTIVE).otherwise(MATERIAL_INPUT_COLOR_INACTIVE));
			} else {
				circle.fillProperty().bind(
						whenActive(port).then(MATERIAL_OUTPUT_COLOR_ACTIVE).otherwise(MATERIAL_OUTPUT_COLOR_INACTIVE));
			}
		} else if (colorPort instanceof EnergyPort) {
			if (colorPort.getDirection().equals(Direction.INPUT.ordinal())) {
				circle.fillProperty()
						.bind(whenActive(port).then(ENERGY_INPUT_COLOR_ACTIVE).otherwise(ENERGY_INPUT_COLOR_INACTIVE));
			} else {
				circle.fillProperty().bind(
						whenActive(port).then(ENERGY_OUTPUT_COLOR_ACTIVE).otherwise(ENERGY_OUTPUT_COLOR_INACTIVE));
			}
		} else if (colorPort instanceof DataPort) {
			if (colorPort.getDirection().equals(Direction.INPUT.ordinal())) {
				circle.fillProperty()
						.bind(whenActive(port).then(DATA_INPUT_COLOR_ACTIVE).otherwise(DATA_INPUT_COLOR_INACTIVE));
			} else {
				circle.fillProperty()
						.bind(whenActive(port).then(DATA_OUTPUT_COLOR_ACTIVE).otherwise(DATA_OUTPUT_COLOR_INACTIVE));
			}
		} else if (colorPort instanceof GenericPort) {
			if (colorPort.getDirection().equals(Direction.INPUT.ordinal())) {
				circle.fillProperty().bind(
						whenActive(port).then(GENERIC_INPUT_COLOR_ACTIVE).otherwise(GENERIC_INPUT_COLOR_INACTIVE));
			} else {
				circle.fillProperty().bind(
						whenActive(port).then(GENERIC_OUTPUT_COLOR_ACTIVE).otherwise(GENERIC_OUTPUT_COLOR_INACTIVE));
			}
		} else {
			throw new IllegalStateException("Port type not supported: " + colorPort.getClass().getName());
		}

		circle.radiusProperty().bind(portRadius);
		circle.setStroke(STROKE);
		circle.setStrokeWidth(STROKE_WIDTH);

		group.getChildren().add(circle);

		// Text flow
		VBox flow = new VBox();

		bind(port, flow, tooltip);

		BooleanBinding isMain = port.parentProperty().isEqualTo(getElement());
		BooleanBinding isNotMain = port.parentProperty().isNotEqualTo(getElement());
		BooleanBinding isInput = port.directionProperty().isEqualTo(Direction.INPUT.ordinal());
		BooleanBinding isOutput = port.directionProperty().isEqualTo(Direction.OUTPUT.ordinal());
		BooleanBinding noIncoming = port.incomingStaticChannelsProperty().sizeProperty().isEqualTo(0);
		BooleanBinding noOutgoing = port.outgoingStaticChannelsProperty().sizeProperty().isEqualTo(0);
		BooleanBinding visible = isMain.and(isInput).and(noOutgoing);
		visible = visible.or(isMain.and(isOutput).and(noIncoming));
		visible = visible.or(isNotMain.and(isInput).and(noIncoming));
		visible = visible.or(isNotMain.and(isOutput).and(noOutgoing));

		flow.setAlignment(Pos.TOP_LEFT);
		flow.visibleProperty().bind(visible);
		flow.translateXProperty().bind(circle.centerXProperty().add(portRadius.divide(2)));
		flow.translateYProperty().bind(circle.centerYProperty().add(portRadius.divide(2)));

		group.getChildren().add(flow);

		// Text
		Text text = new Text();

		bind(port, text, tooltip);

		text.textProperty().bind(Bindings
				.concat(/* "«" + port.getClass().getSimpleName() + "»\n", */port.nameProperty()));

		flow.getChildren().add(text);
	}

	public void transform(Group parent, Rectangle bound, InteractionMaterialPort port) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(Bindings.concat(port.nameProperty(), " : ", port.volumeProperty()));

		// Group
		Group group = new Group();

		parent.getChildren().add(group);

		bind(port, group, tooltip);

		group.translateXProperty().bind(Bindings.add(bound.xProperty(), Bindings.multiply(bound.widthProperty(),
				Bindings.subtract(port.xProperty(), Bindings.divide(port.widthProperty(), 2.)))));
		group.translateYProperty().bind(Bindings.add(bound.yProperty(), Bindings.multiply(bound.heightProperty(),
				Bindings.subtract(port.yProperty(), Bindings.divide(port.heightProperty(), 2.)))));

		// Boundary
		Rectangle rectangle = new Rectangle();

		bind(port, rectangle, tooltip);

		rectangle.widthProperty().bind(Bindings.multiply(bound.widthProperty(), port.widthProperty()));
		rectangle.heightProperty().bind(Bindings.multiply(bound.heightProperty(), port.heightProperty()));
		rectangle.fillProperty().bind(whenActive(port).then(SLOT_FILL_ACTIVE).otherwise(SLOT_FILL_INACTIVE));
		rectangle.setStroke(STROKE);
		rectangle.setStrokeWidth(STROKE_WIDTH);
		rectangle.getStrokeDashArray().addAll(STROKE_DASH);
		rectangle.arcWidthProperty().bind(cornerRadius);
		rectangle.arcHeightProperty().bind(cornerRadius);
		rectangle.setEffect(SHADOW);

		group.getChildren().add(rectangle);

		// Anchors
		addAnchors(group, rectangle, port, tooltip);

		// Flow
		VBox flow = new VBox();

		bind(port, flow, tooltip);

		flow.setAlignment(Pos.CENTER);
		flow.minWidthProperty().bind(rectangle.widthProperty());
		flow.maxWidthProperty().bind(rectangle.widthProperty());
		flow.minHeightProperty().bind(rectangle.heightProperty());
		flow.maxHeightProperty().bind(rectangle.heightProperty());

		group.getChildren().add(flow);

		// Name
		Text text = new Text();

		bind(port, text, tooltip);

		text.setTextAlignment(TextAlignment.CENTER);
		text.textProperty().bind(Bindings
				.concat(/* "«" + port.getClass().getSimpleName() + "»\n", */port.nameProperty()));
		text.wrappingWidthProperty().bind(rectangle.widthProperty().subtract(textPadding.multiply(2)));

		flow.getChildren().add(text);

		// Ports
		for (DefinitionPort child : port.getPorts()) {
			if (!(child instanceof InteractionMaterialPort)) {
				transform(group, rectangle, child);
			}
		}
		port.portsProperty().addListener(new ListChangeListener<DefinitionPort>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends DefinitionPort> change) {
				while (change.next()) {
					for (DefinitionPort removed : change.getRemoved()) {
						unbind(removed);
					}
					for (DefinitionPort added : change.getAddedSubList()) {
						if (show(added)) {
							transform(group, rectangle, added);
						}
					}
				}
			}
		});
	}

	public void transform(Group parent, Rectangle bounds, Component<?> component) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(component.nameProperty());

		// Group
		Group group = new Group();

		parent.getChildren().add(group);

		bind(component, group, tooltip);

		group.translateXProperty().bind(Bindings.add(bounds.xProperty(), Bindings.multiply(bounds.widthProperty(),
				Bindings.subtract(component.xProperty(), Bindings.divide(component.widthProperty(), 2.)))));
		group.translateYProperty().bind(Bindings.add(bounds.yProperty(), Bindings.multiply(bounds.heightProperty(),
				Bindings.subtract(component.yProperty(), Bindings.divide(component.heightProperty(), 2.)))));

		// Boundary
		Rectangle rectangle = new Rectangle();

		bind(component, rectangle, tooltip);

		rectangle.widthProperty().bind(Bindings.multiply(bounds.widthProperty(), component.widthProperty()));
		rectangle.heightProperty().bind(Bindings.multiply(bounds.heightProperty(), component.heightProperty()));
		rectangle.fillProperty()
				.bind(whenActive(component).then(COMPONENT_FILL_ACTIVE).otherwise(COMPONENT_FILL_INACTIVE));
		rectangle.setStroke(STROKE);
		rectangle.setStrokeWidth(STROKE_WIDTH);
		if (component instanceof ReferenceComponent) {
			rectangle.getStrokeDashArray().addAll(STROKE_DASH);
		}
		rectangle.arcWidthProperty().bind(cornerRadius);
		rectangle.arcHeightProperty().bind(cornerRadius);
		rectangle.setEffect(SHADOW);

		group.getChildren().add(rectangle);

		// Anchors
		addAnchors(group, rectangle, component, tooltip);

		// Name
		Text text = new Text();

		bind(component, text, tooltip);

		if (component instanceof ReferenceComponent) {
			text.textProperty().bind(
					component.nameProperty().concat(" : ").concat(((ReferenceComponent) component).templateProperty()));
		} else {
			text.textProperty().bind(Bindings.concat(
					/* "«" + component.getClass().getSimpleName() + "»\n", */component.nameProperty()));
		}
		text.xProperty().bind(textPadding);
		text.yProperty().bind(Bindings.divide(rectangle.heightProperty(), 2));
		text.wrappingWidthProperty().bind(rectangle.widthProperty().subtract(textPadding.multiply(2)));
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);

		group.getChildren().add(text);

		// Ports
		for (Port child : component.getPorts()) {
			if (show(child)) {
				transform(group, rectangle, child);
			}
		}
		component.portsProperty().addListener(new ListChangeListener<Port>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Port> change) {
				while (change.next()) {
					for (Port removed : change.getRemoved()) {
						unbind(removed);
					}
					for (Port added : change.getAddedSubList()) {
						if (show(added)) {
							transform(group, rectangle, added);
						}
					}
				}
			}
		});
	}

	public void transform(Group parent, Rectangle bounds, StaticChannel channel) {
		// Tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty()
				.bind(Bindings.concat(channel.nameProperty(), " : ", channel.getSource(), " -> ", channel.getTarget()));

		// Group
		Group group = new Group();

		parent.getChildren().add(group);

		bind(channel, group, tooltip);

		// Line

		Line line = new Line();

		bind(channel, line, tooltip);

		Port source = channel.getSource();
		Port target = channel.getTarget();

		Group sourceGroup = resolveOnly(source, Group.class);
		Group targetGroup = resolveOnly(target, Group.class);

		List<Group> sourceTrack = track(parent, sourceGroup);
		List<Group> targetTrack = track(parent, targetGroup);

		NumberExpression sourceX = convertXTrack(sourceTrack);
		NumberExpression sourceY = convertYTrack(sourceTrack);
		NumberExpression targetX = convertXTrack(targetTrack);
		NumberExpression targetY = convertYTrack(targetTrack);

		PortPositionListener positionListener = new PortPositionListener(sourceX, sourceY, targetX, targetY,
				portRadius.multiply(1.5), line);

		sourceX.addListener(positionListener);
		sourceY.addListener(positionListener);
		targetX.addListener(positionListener);
		targetY.addListener(positionListener);

		portRadius.addListener(positionListener);

		line.strokeProperty().bind(whenActive(channel).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		line.setStrokeWidth(EDGE_STROKE_WIDTH);

		group.getChildren().add(line);

		// Polygon
		Polygon polygon = new Polygon(EDGE_ARROW_COORDINATES);

		bind(channel, polygon, tooltip);

		Rotate rotate = new Rotate();

		polygon.translateXProperty().bind(line.endXProperty());
		polygon.translateYProperty().bind(line.endYProperty());
		polygon.getTransforms().add(rotate);
		polygon.fillProperty().bind(whenActive(channel).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		polygon.strokeProperty().bind(whenActive(channel).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		polygon.setStrokeWidth(STROKE_WIDTH);

		group.getChildren().add(polygon);

		LineAngleListener angleListener = new LineAngleListener(line, rotate.angleProperty());

		line.startXProperty().addListener(angleListener);
		line.startYProperty().addListener(angleListener);
		line.endXProperty().addListener(angleListener);
		line.endYProperty().addListener(angleListener);

		// Flow
		VBox flow = new VBox();

		bind(channel, flow, tooltip);

		flow.setAlignment(Pos.CENTER);
		flow.setMinWidth(CHANNEL_TEXT_WIDTH);
		flow.setMaxWidth(CHANNEL_TEXT_WIDTH);
		flow.setMinHeight(CHANNEL_TEXT_WIDTH);
		flow.setMaxHeight(CHANNEL_TEXT_WIDTH);
		flow.translateXProperty()
				.bind(line.startXProperty().add(line.endXProperty()).divide(2).subtract(CHANNEL_TEXT_WIDTH / 2));
		flow.translateYProperty()
				.bind(line.startYProperty().add(line.endYProperty()).divide(2).subtract(CHANNEL_TEXT_WIDTH / 2));

		group.getChildren().add(flow);

		// Text
		Text text = new Text();

		bind(channel, text, tooltip);

		text.textProperty().bind(Bindings
				.concat(/* "«" + channel.getClass().getSimpleName() + "»\n", */channel.nameProperty()));
		text.setWrappingWidth(CHANNEL_TEXT_WIDTH * 2);
		text.fillProperty().bind(whenActive(channel).then(EDGE_COLOR_ACTIVE).otherwise(EDGE_COLOR_INACTIVE));
		text.setTextAlignment(TextAlignment.CENTER);

		flow.getChildren().add(text);
	}

	private boolean show(Port port) {
		return !(port instanceof InteractionMaterialPort) && !(port instanceof ReferencePort
				&& ((ReferencePort) port).getPortImplementation() instanceof InteractionMaterialPort);
	}

}
