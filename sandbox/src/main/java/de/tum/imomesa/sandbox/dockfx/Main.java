package de.tum.imomesa.sandbox.dockfx;

import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		// DockFX.main(args);

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Image image = new Image("icon.png");

		DockPane dockPane = new DockPane();

		ImageView imageViewOne = new ImageView(image);
		DockNode dockNodeOne = new DockNode(new Label("Test"), "Test", imageViewOne);
		dockNodeOne.dock(dockPane, DockPos.CENTER);

		ImageView imageViewTwo = new ImageView(image);
		DockNode dockNodeTwo = new DockNode(new Label("Test 2"), "Test 2", imageViewTwo);
		dockNodeTwo.dock(dockPane, DockPos.LEFT);

		Scene scene = new Scene(dockPane);

		primaryStage.setTitle("DockFX");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

		DockPane.initializeDefaultUserAgentStylesheet();
	}

}
