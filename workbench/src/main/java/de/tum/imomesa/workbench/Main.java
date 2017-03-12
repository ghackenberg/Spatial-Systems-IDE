package de.tum.imomesa.workbench;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.FadeApp.InitCompletionHandler;
import de.tum.imomesa.workbench.commons.events.ProgramEndEvent;
import de.tum.imomesa.workbench.commons.events.ProgramStartEvent;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Main Application that initializes the UI
 */
public class Main extends Application {

	// static

	private static String DEFAULT_RUN_PATH = "run/default";

	public static String NAME = "MACON Workbench";
	public static String DESCRIPTION = "A tool for consistent cross-discipline MAnufacturing CONception";

	public static String SESSION_PATH;
	public static File SESSION_FOLDER;

	public static void main(String[] args) {
		// initialize session path
		SESSION_PATH = ((args.length == 0) ? DEFAULT_RUN_PATH : args[0]) + "/session_"
				+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "/";
		// initialize session folder
		SESSION_FOLDER = new File(SESSION_PATH);
		SESSION_FOLDER.mkdirs();
		// launch application
		Application.launch(Main.class, args);
	}

	// dynamic

	private String runPath;
	private File runFolder;

	private File logFile;

	private SplashScreenCreator splashScreen;

	private Stage mainStage;

	@Override
	public void init() {
		if (getParameters().getUnnamed().size() > 0) {
			runPath = getParameters().getUnnamed().get(0);
		} else {
			runPath = DEFAULT_RUN_PATH;
		}
		runFolder = new File(runPath);
		runFolder.mkdirs();
		// create storage manager
		StorageManager.createInstance(runFolder);
		// initialize log file
		logFile = new File(runFolder, "event.log");
		// initialize log hander
		EventBus.getInstance().subscribe(new LogHandler(logFile));
		EventBus.getInstance().publish(new ProgramStartEvent());
		// initialize splash screen
		splashScreen = new SplashScreenCreator();
		splashScreen.init();
	}

	@Override
	public void start(Stage initStage) {
		// update init stage
		initStage.setTitle(NAME + " - Splash");
		initStage.getIcons().add(new Image("images/icon.png"));

		// init database task
		Task<Boolean> initDatabase = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				if (StorageManager.getInstance().getWorkspace() == null) {
					return false;
				} else {
					StorageManager.getInstance().getWorkspace().accept(new SyntacticCheckVisitor());
					return true;
				}
			}
		};

		// show splash screen
		splashScreen.showSplash(initStage, initDatabase, new InitCompletionHandler() {
			@Override
			public void complete() {
				showMainStage();
			}
		});

		// run database task
		new Thread(initDatabase).start();
	}

	private void showMainStage() {
		try {
			// load fxml
			Parent root = FXMLLoader.load(getClass().getResource("/views/Main.fxml"));

			// create scene
			Scene s = new Scene(root, 1000, 650);

			// create stage
			mainStage = new Stage(StageStyle.DECORATED);
			mainStage.setTitle(NAME + " - Main");
			mainStage.setIconified(true);
			mainStage.getIcons().add(new Image("images/icon.png"));
			mainStage.setMaximized(true);
			mainStage.setScene(s);
			mainStage.show();
			mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// close connection to database
					StorageManager.getInstance().close();
					// log program end
					EventBus.getInstance().publish(new ProgramEndEvent());
				}
			});

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
