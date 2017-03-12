package de.tum.imomesa.sandbox.jcsg;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Sphere;
import eu.mihosoft.vrl.v3d.Transform;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		CSG cube = new Cube(2).toCSG();
		CSG sphere = new Sphere(1.25).toCSG();

		CSG cubePlusSphere = cube.union(sphere);
		CSG cubeMinusSphere = cube.difference(sphere);
		CSG cubeIntersectSphere = cube.intersect(sphere);

		CSG union = cube.union(sphere.transformed(Transform.unity().translateX(3)))
				.union(cubePlusSphere.transformed(Transform.unity().translateX(6)))
				.union(cubeMinusSphere.transformed(Transform.unity().translateX(9)))
				.union(cubeIntersectSphere.transformed(Transform.unity().translateX(12)));

		Camera camera = new PerspectiveCamera(true);
		camera.setNearClip(1.0);
		camera.setFarClip(100.0);
		camera.setTranslateZ(-20.0);
		camera.setTranslateX(6);

		Group root = new Group();
		root.getChildren().add(camera);
		root.getChildren().addAll(union.toJavaFXMesh().getAsMeshViews());

		Scene scene = new Scene(root, 1000, 600, true);
		scene.setFill(Color.GRAY);
		scene.setCamera(camera);

		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

}
