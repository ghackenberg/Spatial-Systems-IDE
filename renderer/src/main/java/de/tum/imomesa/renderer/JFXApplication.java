package de.tum.imomesa.renderer;

import de.tum.imomesa.renderer.nodes.JMECanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JFXApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		JMECanvas canvas = new JMECanvas();
		
		StackPane pane = new StackPane();
		pane.getChildren().add(canvas);
		
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
		
		Scene scene = new Scene(pane, 640, 480);
		
		primaryStage.setTitle("Test");
		primaryStage.setScene(scene);
		primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
	}

}
