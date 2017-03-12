package de.tum.imomesa.workbench.controllers;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.workbench.scenes.Configuration;
import de.tum.imomesa.workbench.scenes.helpers.HelperCoordinateSystem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public abstract class AbstractSceneController implements de.tum.imomesa.core.events.EventHandler {

	private static final double ROTATE_UNIT = 2;
	private static final double TRANSLATE_UNIT = 1;

	protected Component<?> data;

	protected Group world;
	protected Rotate rotationTransformX;
	protected Rotate rotationTransformY;
	protected Translate translateDistance;
	protected Group componentVisualization;

	private boolean rotating = false;
	private boolean translating = false;
	private double previous_x;
	private double previous_y;
	private double previous_angle_x;
	private double previous_angle_y;
	private double previous_translate_x;
	private double previous_translate_y;

	protected ObjectProperty<Element> selected = new SimpleObjectProperty<>();

	@FXML
	protected Pane pane;
	@FXML
	protected CheckBox coordinates;
	@FXML
	protected CheckBox outline;
	@FXML
	protected CheckBox highlight;

	protected Configuration config;

	@FXML
	public void initialize() {
		config = new Configuration(outline.selectedProperty(), highlight.selectedProperty(), selected);
		// create scene
		createScene();
		// subscribe to event
		EventBus.getInstance().subscribe(this);
	}

	protected void createScene() {
		rotationTransformX = new Rotate(-45, new Point3D(1, 0, 0));
		rotationTransformY = new Rotate(-45, new Point3D(0, 1, 0));
		translateDistance = new Translate(0, 0, -200);

		// define camera
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setNearClip(1.0);
		camera.setFarClip(1000.0);
		camera.getTransforms().add(new Rotate(180, new Point3D(1, 0, 0)));
		camera.getTransforms().add(rotationTransformY);
		camera.getTransforms().add(rotationTransformX);
		camera.getTransforms().add(translateDistance);

		// create group
		world = new Group();
		world.getChildren().add(camera);

		// add coordinate system
		world.getChildren().add(HelperCoordinateSystem.getCoordinates(coordinates.selectedProperty()));

		// visualization
		componentVisualization = new Group();
		world.getChildren().add(componentVisualization);

		// create and set scene
		SubScene subScene = new SubScene(world, 600, 500, true, SceneAntialiasing.BALANCED);
		subScene.heightProperty().bind(pane.heightProperty());
		subScene.widthProperty().bind(pane.widthProperty());
		subScene.setCamera(camera);

		pane.getChildren().add(subScene);
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				previous_x = event.getScreenX();
				previous_y = event.getScreenY();

				previous_angle_x = rotationTransformX.getAngle();
				previous_angle_y = rotationTransformY.getAngle();

				previous_translate_x = translateDistance.getX();
				previous_translate_y = translateDistance.getY();

				rotating = event.isPrimaryButtonDown();
				translating = event.isSecondaryButtonDown();
			}
		});
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double diff_x = event.getScreenX() - previous_x;
				double diff_y = event.getScreenY() - previous_y;

				if (rotating) {
					double angle_x = ((previous_angle_x - diff_y / 5) + 360) % 360;
					double angle_y = ((previous_angle_y + diff_x / 5) + 360) % 360;

					if (!event.isAltDown()) {
						angle_x = Math.round(angle_x / ROTATE_UNIT) * ROTATE_UNIT;
						angle_y = Math.round(angle_y / ROTATE_UNIT) * ROTATE_UNIT;
					}

					rotationTransformX.setAngle(angle_x);
					rotationTransformY.setAngle(angle_y);
				}
				if (translating) {
					double x = previous_translate_x - diff_x / 10;
					double y = previous_translate_y - diff_y / 10;

					if (!event.isAltDown()) {
						x = Math.round(x / TRANSLATE_UNIT) * TRANSLATE_UNIT;
						y = Math.round(y / TRANSLATE_UNIT) * TRANSLATE_UNIT;
					}

					translateDistance.setX(x);
					translateDistance.setY(y);
				}

				/*
				 * previous_x = event.getScreenX(); previous_y =
				 * event.getScreenY();
				 */
			}
		});
		pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				rotating = false;
				translating = false;
			}
		});
		pane.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				translateDistance.setZ(translateDistance.getZ() + event.getDeltaY());
			}

		});
	}
}
