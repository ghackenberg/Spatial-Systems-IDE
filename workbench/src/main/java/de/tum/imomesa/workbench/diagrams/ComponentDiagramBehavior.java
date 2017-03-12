package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.ports.Port.Direction;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ComponentDiagramBehavior extends AbstractDiagramBehavior<DefinitionComponent> {
	
	private double dx = 0;
	private double dy = 0;

	@Override
	public void handleMousePressed(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Component) {
			Component<?> component = (Component<?>) element;
			
			Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
			Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());
			
			dx = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - component.getX();
			dy = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - component.getY();
		} else if (getDiagram().getPressedElement() instanceof InteractionMaterialPort) {
			InteractionMaterialPort port = (InteractionMaterialPort) element;
			
			Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
			Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

			dx = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - port.getX();
			dy = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - port.getY();
		} else if (getDiagram().getPressedElement() instanceof Port) {
			// ignore
		}
	}

	@Override
	public void handleMouseDragged(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Component) {
			if (!getDiagram().getPressedElement().equals(getDiagram().getElement())) {
				Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
				Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

				double x = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - dx;
				double y = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - dy;
				
				if (!event.isAltDown()) {
					x = Math.round(x * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
					y = Math.round(y * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
				}

				Component<?> component = (Component<?>) getDiagram().getPressedElement();

				if (x >= 0 && x <= 1) {
					component.setX(x);
				}
				if (y >= 0 && y <= 1) {
					component.setY(y);
				}
			} else {
				// ignore
			}
		} else if (getDiagram().getPressedElement() instanceof InteractionMaterialPort) {
			Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
			Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

			double x = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - dx;
			double y = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - dy;
			
			if (!event.isAltDown()) {
				x = Math.round(x * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
				y = Math.round(y * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
			}

			InteractionMaterialPort port = (InteractionMaterialPort) getDiagram().getPressedElement();

			if (x >= 0 && x <= 1) {
				port.setX(x);
			}
			if (y >= 0 && y <= 1) {
				port.setY(y);
			}
		} else if (getDiagram().getPressedElement() instanceof Port) {
			if (!event.isControlDown()) {
				Element parent = getDiagram().getPressedElement().getParent();
				Rectangle rectangle = getDiagram().resolveOnly(parent, Rectangle.class);
				Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

				double dx = event.getSceneX() - (bounds.getMinX() + bounds.getMaxX()) / 2;
				double dy = event.getSceneY() - (bounds.getMinY() + bounds.getMaxY()) / 2;
				
				dx /= bounds.getWidth() / 2;
				dy /= bounds.getHeight() / 2;
				
				double angle = ((Math.atan2(dy, dx) / Math.PI * 180. + 45.) + 360.) % 360;
				
				if (angle < 90 * 1) {
					double ratio = 1. / dx;
					double factor = (dy * ratio + 1.) / 2.;
					if (!event.isAltDown()) {
						factor = Math.round(factor * Parameters.RECTANGLE_STEPS) / Parameters.RECTANGLE_STEPS;
					}
					angle = 90 * 0 + 90 * factor;
				} else if (angle < 90 * 2) {
					double ratio = 1. / dy;
					double factor = (dx * ratio - 1.) / 2.;
					if (!event.isAltDown()) {
						factor = Math.round(factor * Parameters.RECTANGLE_STEPS) / Parameters.RECTANGLE_STEPS;
					}
					angle = 90 * 1 - 90 * factor;
				} else if (angle < 90 * 3) {
					double ratio = -1. / dx;
					double factor = (dy * ratio - 1.) / 2.;
					if (!event.isAltDown()) {
						factor = Math.round(factor * Parameters.RECTANGLE_STEPS) / Parameters.RECTANGLE_STEPS;
					}
					angle = 90 * 2 - 90 * factor;
				} else if (angle < 90 * 4) {
					double ratio = -1. / dy;
					double factor = (dx * ratio + 1.) / 2.;
					if (!event.isAltDown()) {
						factor = Math.round(factor * Parameters.RECTANGLE_STEPS) / Parameters.RECTANGLE_STEPS;
					}
					angle = 90 * 3 + 90 * factor; 
				} else {
					throw new IllegalStateException();
				}

				Port port = (Port) getDiagram().getPressedElement();

				port.setAngle(angle);
			} else {
				// ignore
			}
		}
	}

	@Override
	public void handleMouseReleased(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Component) {
			// ignore
		} else if (getDiagram().getPressedElement() instanceof InteractionMaterialPort) {
			// ignore
		} else if (getDiagram().getPressedElement() instanceof Port) {
			if (event.isControlDown()) {
				if (getDiagram().getReleasedElement() instanceof Port) {
					Port source = (Port) getDiagram().getPressedElement();
					Port target = (Port) getDiagram().getReleasedElement();

					if (!target.getWriteType().contains(source.getReadType())) {
						alert("Cannot create static channel!", "Types incompatible!");
						return;
					}
					if (target.getIncomingStaticChannels().size() > 0) {
						alert("Cannot create static channel!", "Too many incoming channels!");
						return;
					}
					if (target.getDirection().equals(Direction.INPUT.ordinal())
							&& target.getParent().equals(getDiagram().getElement())) {
						alert("Cannot create static channel!", "Parent input cannot be target!");
						return;
					}
					if (source.getDirection().equals(Direction.OUTPUT.ordinal())
							&& source.getParent().equals(getDiagram().getElement())) {
						alert("Cannot create static channel!", "Parent output cannot be source!");
						return;
					}
					if (source.getDirection().equals(Direction.INPUT.ordinal())
							&& target.getDirection().equals(Direction.INPUT.ordinal())
							&& !source.getParent().equals(getDiagram().getElement())) {
						alert("Cannot create static channel!", "Inputs have to be forwarded!");
						return;
					}
					if (source.getDirection().equals(Direction.OUTPUT.ordinal())
							&& target.getDirection().equals(Direction.OUTPUT.ordinal())
							&& !target.getParent().equals(getDiagram().getElement())) {
						alert("Cannot create static channel!", "Outputs have to be forwarded!");
						return;
					}
					if (source.getDirection().equals(Direction.INPUT.ordinal())
							&& target.getDirection().equals(Direction.OUTPUT.ordinal())) {
						alert("Cannot create static channel!", "Inputs cannot be forwarded to outputs directly!");
						return;
					}
					if (source.getDirection().equals(Direction.OUTPUT.ordinal())
							&& target.getDirection().equals(Direction.INPUT.ordinal())
							&& source.getParent().equals(target.getParent())) {
						alert("Cannot create static channel!", "Channels has to connect two different components!");
						return;
					}

					StaticChannel channel = new StaticChannel();

					channel.setName("New static channel");
					channel.setSource(source);
					channel.setTarget(target);
					channel.setParent(getDiagram().getElement());

					source.getOutgoingStaticChannels().add(channel);
					target.getIncomingStaticChannels().add(channel);

					getDiagram().getElement().getChannels().add(channel);
					
					channel.accept(new SyntacticCheckVisitor());
				} else {
					alert("Cannot create static channel!", "Drop target no port!");
				}
			} else {
				// ignore
			}
		}
	}

}
