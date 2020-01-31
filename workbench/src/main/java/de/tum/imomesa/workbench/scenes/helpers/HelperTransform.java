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
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
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
				PointChangeListener listener = new PointChangeListener(rotate.axisProperty(), ((RotationTransform) transform).rotationAxeProperty());
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

	public static void paintTransform(ModelPath<Transform> modelPath, ObjectProperty<Element> selected, ScenePath sceneParentCtx) {
		if (modelPath.element instanceof TranslationTransform) {
			paintTranslationTransform(modelPath.cast(TranslationTransform.class), selected, sceneParentCtx);
		} else if (modelPath.element instanceof RotationTransform) {
			paintRotationTransform(modelPath.cast(RotationTransform.class), selected, sceneParentCtx);
		} else {
			throw new IllegalStateException("Transform type not supported: " + modelPath.element.getClass().getName());
		}
	}

	private static void paintTranslationTransform(ModelPath<TranslationTransform> modelPath, ObjectProperty<Element> selected, ScenePath sceneParentCtx) {
		paintVector(new ModelPath<>(modelPath, modelPath.element.getVector()), selected, sceneParentCtx);
	}

	private static void paintRotationTransform(ModelPath<RotationTransform> modelPath, ObjectProperty<Element> selected, ScenePath scenePath) {
		paintVector(new ModelPath<>(modelPath, modelPath.element.getRotationAxe()), selected, scenePath);
	}

	private static void paintVector(ModelPath<Point> modelPath, ObjectProperty<Element> selected, ScenePath scenePath) {
		// calculate angle
		double angleY = -modelPath.element.getAngleDegree(new Point(0, 1, 0));

		Point axe = modelPath.element.crossProduct(new Point(0, 1, 0));
		Rotate rotation = new Rotate(angleY, new Point3D(axe.getX(), axe.getY(), axe.getZ()));

		AngleChangeListener listenerAngleY = new AngleChangeListener(rotation, modelPath.element, new Point(0, 1, 0));

		ListenerManager.getListenerRemover().addListener(modelPath.element.xProperty(), listenerAngleY);
		ListenerManager.getListenerRemover().addListener(modelPath.element.yProperty(), listenerAngleY);
		ListenerManager.getListenerRemover().addListener(modelPath.element.zProperty(), listenerAngleY);

		// Create group

		final Group solidChild = new Group();

		solidChild.getChildren().add(createArrowBody(modelPath, rotation, DrawMode.FILL, CullFace.BACK, Color.WHITE, selected));
		solidChild.getChildren().add(createArrowHead(modelPath, rotation, DrawMode.FILL, CullFace.BACK, Color.WHITE, selected));

		solidChild.getChildren().add(createArrowBody(modelPath, rotation, DrawMode.LINE, CullFace.BACK, Color.BLACK, selected));
		solidChild.getChildren().add(createArrowHead(modelPath, rotation, DrawMode.LINE, CullFace.BACK, Color.BLACK, selected));

		scenePath.solidParent.getChildren().add(solidChild);
	}

	private static Node createArrowBody(ModelPath<Point> modelPath, Rotate rotation, DrawMode mode, CullFace cull, Color color, ObjectProperty<Element> selected) {
		if (!modelPath.path.contains(selected.get())) {
			color = color.darker();
		}

		Cylinder shape = new Cylinder(0.5, modelPath.element.getLength(), 5);

		shape.setDrawMode(mode);
		shape.setCullFace(cull);
		shape.setMaterial(new PhongMaterial(color));

		LengthChangeListener listener = new LengthChangeListener(modelPath.element, shape.heightProperty());

		ListenerManager.getListenerRemover().addListener(modelPath.element.xProperty(), listener);
		ListenerManager.getListenerRemover().addListener(modelPath.element.yProperty(), listener);
		ListenerManager.getListenerRemover().addListener(modelPath.element.zProperty(), listener);

		Translate translation = new Translate(0, 0, 0);

		translation.yProperty().bind(shape.heightProperty().divide(2));

		shape.getTransforms().add(rotation);
		shape.getTransforms().add(translation);

		return shape;
	}

	private static Node createArrowHead(ModelPath<Point> modelPath, Rotate rotation, DrawMode mode, CullFace cull, Color color, ObjectProperty<Element> selected) {
		if (!modelPath.path.contains(selected.get())) {
			color = color.darker();
		}

		Cone shape = new Cone(1.0, 2.0, 10);

		shape.setDrawMode(mode);
		shape.setCullFace(cull);
		shape.setMaterial(new PhongMaterial(color));

		Translate translation = new Translate();

		translation.xProperty().bind(modelPath.element.xProperty());
		translation.yProperty().bind(modelPath.element.yProperty());
		translation.zProperty().bind(modelPath.element.zProperty());

		shape.getTransforms().add(translation);
		shape.getTransforms().add(rotation);

		return shape;
	}

}
