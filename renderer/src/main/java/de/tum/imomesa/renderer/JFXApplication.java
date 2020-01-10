package de.tum.imomesa.renderer;

import com.jme3.jfx.injfx.JmeToJfxIntegrator;
import com.jme3.system.AppSettings;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JFXApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Canvas canvas = new Canvas();
		
		StackPane pane = new StackPane();
		pane.getChildren().add(canvas);
		
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
		
		Scene scene = new Scene(pane, 640, 480);
		
		primaryStage.setTitle("Test");
		primaryStage.setScene(scene);
		primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));

		AppSettings settings = JmeToJfxIntegrator.prepareSettings(new AppSettings(true), 60);
		
		JMEApplication jme = new JMEApplication();
		jme.setSettings(settings);
		jme.setShowSettings(false);
		jme.setDisplayFps(false);
		jme.setDisplayStatView(false);
		
		JmeToJfxIntegrator.startAndBindMainViewPort(jme, canvas, Thread::new);
	}

}
