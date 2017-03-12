package de.tum.imomesa.workbench;

import de.tum.imomesa.workbench.FadeApp.InitCompletionHandler;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreenCreator {

	// data
	private Pane splashLayout;
	private static final int SPLASH_WIDTH = 620;
	private static final int SPLASH_HEIGHT = 350;

	// init splash screen
	public void init() {
		ImageView splash = new ImageView(new Image("images/splash.png"));
		splash.setPreserveRatio(true);
		splash.setFitWidth(SPLASH_WIDTH);
		
		LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.STEELBLUE));

		splashLayout = new VBox();
		splashLayout.setPadding(new Insets(20));
		splashLayout.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(0), new Insets(0))));
		splashLayout.getChildren().add(splash);
	}

	// show splash screen
	void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				initStage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> initStage.hide());
				fadeSplash.play();

				initCompletionHandler.complete();
			}
		});

		Scene splashScene = new Scene(splashLayout);
		initStage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		initStage.setScene(splashScene);
		initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		initStage.show();
	}

}
