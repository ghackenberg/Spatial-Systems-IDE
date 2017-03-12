package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public abstract class ExecutableDiagramBehavior<L extends Label, T extends Transition<L>, E extends Executable<L, T>>
		extends AbstractDiagramBehavior<E> {

	private double dx = 0;
	private double dy = 0;
	private L label = null;

	@SuppressWarnings("unchecked")
	@Override
	public void handleMousePressed(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Label) {
			if (!event.isControlDown()) {
				Label label = (Label) element;

				Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
				Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

				dx = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - label.getX();
				dy = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - label.getY();
			} else {
				// ignore
			}
		} else if (getDiagram().getPressedElement() instanceof Transition) {
			Element parent = getDiagram().resolve(node.getParent().getParent());
			if (parent instanceof Label) {
				label = (L) parent;
			} else {
				label = null;
			}
		}
	}

	@Override
	public void handleMouseDragged(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Label) {
			if (!event.isControlDown()) {
				Rectangle rectangle = getDiagram().resolveOnly(getDiagram().getElement(), Rectangle.class);
				Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

				double x = (event.getSceneX() - bounds.getMinX()) / bounds.getWidth() - dx;
				double y = (event.getSceneY() - bounds.getMinY()) / bounds.getHeight() - dy;

				if (!event.isAltDown()) {
					x = Math.round(x * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
					y = Math.round(y * Parameters.GRID_STEPS) / Parameters.GRID_STEPS;
				}

				Label label = (Label) getDiagram().getPressedElement();

				if (x >= 0 && x <= 1) {
					label.setX(x);
				}
				if (y >= 0 && y <= 1) {
					label.setY(y);
				}
			} else {
				// ignore
			}
		} else if (getDiagram().getPressedElement() instanceof Transition) {
			if (label != null) {
				Ellipse rectangle = getDiagram().resolve(label, Ellipse.class).get(0);
				Bounds bounds = rectangle.localToScene(rectangle.getBoundsInLocal());

				double dx = event.getSceneX() - (bounds.getMinX() + bounds.getMaxX()) / 2;
				double dy = event.getSceneY() - (bounds.getMinY() + bounds.getMaxY()) / 2;

				double angle = Math.atan2(dy / rectangle.getRadiusY(), dx / rectangle.getRadiusX());

				angle = angle / Math.PI * 180;

				if (!event.isAltDown()) {
					angle = Math.floor(angle / 360. * Parameters.ELLIPSE_STEPS) / Parameters.ELLIPSE_STEPS * 360.;
				}

				@SuppressWarnings("unchecked")
				T transition = (T) getDiagram().getPressedElement();

				if (transition.getSourceLabel().equals(label)) {
					transition.setSourceAngle(angle);
				} else if (transition.getTargetLabel().equals(label)) {
					transition.setTargetAngle(angle);
				} else {
					throw new IllegalStateException();
				}
			} else {
				// ignore
			}
		}
	}

	@Override
	public void handleMouseReleased(MouseEvent event, Element element, Node node) {
		if (getDiagram().getPressedElement() instanceof Label) {
			if (!event.isControlDown()) {
				// ignore
			} else {
				if (getDiagram().getReleasedElement() instanceof Label) {
					@SuppressWarnings("unchecked")
					L source = (L) getDiagram().getPressedElement();
					@SuppressWarnings("unchecked")
					L target = (L) getDiagram().getReleasedElement();

					T transition = createTransition();

					transition.setName("New transition");
					transition.setSourceLabel(source);
					transition.setTargetLabel(target);
					transition.setParent(getDiagram().getElement());

					getDiagram().getElement().getTransitions().add(transition);
					
					transition.accept(new SyntacticCheckVisitor());
				} else {
					alert("Cannot create transition!", "Drop target no label!");
				}
			}
		} else if (getDiagram().getPressedElement() instanceof Transition) {
			// ignore
		}
	}

	protected abstract T createTransition();

}
