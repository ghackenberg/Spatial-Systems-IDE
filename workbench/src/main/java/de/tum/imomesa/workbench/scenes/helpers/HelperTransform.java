package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.model.transforms.TranslationTransform;
import de.tum.imomesa.workbench.scenes.listeners.AngleChangeListener;
import de.tum.imomesa.workbench.scenes.listeners.LengthChangeListener;
import de.tum.imomesa.workbench.scenes.listeners.PointChangeListener;
import de.tum.imomesa.workbench.scenes.managers.ListenerManager;
import de.tum.imomesa.workbench.scenes.nodes.Cone;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class HelperTransform {

	public static void addTransforms(Node shape, List<Transform> transforms) {
		for (Transform transform : transforms) {
			// RotationTransform
			if (transform instanceof RotationTransform) {
				Point vector = ((RotationTransform) transform).getRotationAxe();

				// bind angle
				Rotate rotate = new Rotate();
				rotate.angleProperty().bind(((RotationTransform) transform).angleProperty());
				rotate.setAxis(new Point3D(vector.getX(), vector.getY(), vector.getZ()));

				// add listener
				// store pointer on listener
				PointChangeListener listener = new PointChangeListener(rotate.axisProperty(),
						((RotationTransform) transform).rotationAxeProperty());
				ListenerManager.getListenerRemover().addListener(vector.xProperty(), listener);
				ListenerManager.getListenerRemover().addListener(vector.yProperty(), listener);
				ListenerManager.getListenerRemover().addListener(vector.zProperty(), listener);

				shape.getTransforms().add(rotate);
			} else if (transform instanceof TranslationTransform) {
				Point vector = ((TranslationTransform) transform).getVector();

				Translate translate = new Translate();
				translate.xProperty().bind(vector.xProperty());
				translate.yProperty().bind(vector.yProperty());
				translate.zProperty().bind(vector.zProperty());

				shape.getTransforms().add(translate);
			} else {
				throw new IllegalStateException("Transform not supported: " + transform.getClass().getName());
			}
		}
	}

	public static Node paintTransform(List<Element> context, Transform transform, ObjectProperty<Element> selected) {
		if (transform instanceof TranslationTransform) {
			TranslationTransform translation = (TranslationTransform) transform;
			return paintVector(translation.append(context), translation.getVector(), selected);
		} else if (transform instanceof RotationTransform) {
			RotationTransform rotation = (RotationTransform) transform;
			return paintVector(rotation.append(context), rotation.getRotationAxe(), selected);
		} else {
			throw new IllegalStateException("Transform type not supported: " + transform.getClass().getName());
		}
	}

	private static Node paintVector(List<Element> context, Point point, ObjectProperty<Element> selected) {
		// calculate angle
		double angleY = -point.getAngleDegree(new Point(0, 1, 0));

		Point axe = point.crossProduct(new Point(0, 1, 0));
		Rotate rotation = new Rotate(angleY, new Point3D(axe.getX(), axe.getY(), axe.getZ()));

		AngleChangeListener listenerAngleY = new AngleChangeListener(rotation, point, new Point(0, 1, 0));

		ListenerManager.getListenerRemover().addListener(point.xProperty(), listenerAngleY);
		ListenerManager.getListenerRemover().addListener(point.yProperty(), listenerAngleY);
		ListenerManager.getListenerRemover().addListener(point.zProperty(), listenerAngleY);

		// Create group

		Group group = new Group();

		group.getChildren().add(createArrowBody(context, point, rotation, DrawMode.FILL, CullFace.BACK, Color.WHITE, selected));
		group.getChildren().add(createArrowHead(context, point, rotation, DrawMode.FILL, CullFace.BACK, Color.WHITE, selected));

		group.getChildren().add(createArrowBody(context, point, rotation, DrawMode.LINE, CullFace.BACK, Color.BLACK, selected));
		group.getChildren().add(createArrowHead(context, point, rotation, DrawMode.LINE, CullFace.BACK, Color.BLACK, selected));

		return group;
	}

	private static Node createArrowBody(List<Element> context, Point point, Rotate rotation, DrawMode mode,
			CullFace cull, Color color, ObjectProperty<Element> selected) {
		if (!point.getPath().contains(selected.get())) {
			color = color.darker();
		}

		Cylinder shape = new Cylinder(0.5, point.getLenght(), 5);

		shape.setDrawMode(mode);
		shape.setCullFace(cull);
		shape.setMaterial(new PhongMaterial(color));

		LengthChangeListener listener = new LengthChangeListener(point, shape.heightProperty());

		ListenerManager.getListenerRemover().addListener(point.xProperty(), listener);
		ListenerManager.getListenerRemover().addListener(point.yProperty(), listener);
		ListenerManager.getListenerRemover().addListener(point.zProperty(), listener);

		Translate translation = new Translate(0, 0, 0);

		translation.yProperty().bind(shape.heightProperty().divide(2));

		shape.getTransforms().add(rotation);
		shape.getTransforms().add(translation);

		return shape;
	}

	private static Node createArrowHead(List<Element> context, Point point, Rotate rotation, DrawMode mode,
			CullFace cull, Color color, ObjectProperty<Element> selected) {
		if (!point.getPath().contains(selected.get())) {
			color = color.darker();
		}

		Cone shape = new Cone(1.0, 2.0, 10);

		shape.setDrawMode(mode);
		shape.setCullFace(cull);
		shape.setMaterial(new PhongMaterial(color));

		Translate translation = new Translate();

		translation.xProperty().bind(point.xProperty());
		translation.yProperty().bind(point.yProperty());
		translation.zProperty().bind(point.zProperty());

		shape.getTransforms().add(translation);
		shape.getTransforms().add(rotation);

		return shape;
	}

}
