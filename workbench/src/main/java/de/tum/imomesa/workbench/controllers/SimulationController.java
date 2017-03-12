package de.tum.imomesa.workbench.controllers;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.Main;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.simulation.AttributeController;
import de.tum.imomesa.workbench.controllers.simulation.EditorController;
import de.tum.imomesa.workbench.controllers.simulation.ExplorerController;
import de.tum.imomesa.workbench.controllers.simulation.MarkerController;
import de.tum.imomesa.workbench.controllers.simulation.ResultController;
import de.tum.imomesa.workbench.controllers.simulation.SceneController;
import de.tum.imomesa.workbench.simulations.threads.AnimationThread;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class SimulationController implements EventHandler {

	private AnimationThread animation;

	private Simulator simulator;

	@FXML
	private BorderPane bpSimulation;
	@FXML
	private Button bForward;
	@FXML
	private Button bPlay;
	@FXML
	private Button bPause;
	@FXML
	private Button bBackward;
	@FXML
	private Spinner<Integer> spIdleTime;
	@FXML
	private Slider sStep;

	@FXML
	private ExplorerController embeddedExplorerController;
	@FXML
	private EditorController embeddedEditorController;
	@FXML
	private SceneController embeddedSceneController;
	@FXML
	private MarkerController embeddedMarkerController;
	@FXML
	private ResultController embeddedResultController;
	@FXML
	private AttributeController embeddedAttributeController;

	@FXML
	private Button buttonLayoutLarge;
	@FXML
	private Button buttonLayoutMedium;
	@FXML
	private Button buttonLayoutSmall;

	@FXML
	private GridPane gridPane;
	@FXML
	private TabPane tabPaneExplorer;
	@FXML
	private TabPane tabPaneEditor;
	@FXML
	private TabPane tabPaneScene;
	@FXML
	private TabPane tabPaneMarker;
	@FXML
	private TabPane tabPaneResult;
	@FXML
	private TabPane tabPaneAttribute;

	// private Tab tabExplorer;
	private Tab tabEditor;
	private Tab tabScene;
	private Tab tabMarker;
	private Tab tabResult;
	// private Tab tabAttribute;

	@FXML
	public void initialize() {
		// tabExplorer = tabPaneExplorer.getTabs().get(0);
		tabEditor = tabPaneEditor.getTabs().get(0);
		tabScene = tabPaneScene.getTabs().get(0);
		tabMarker = tabPaneMarker.getTabs().get(0);
		tabResult = tabPaneResult.getTabs().get(0);
		// tabAttribute = tabPaneAttribute.getTabs().get(0);
	}

	@FXML
	public void layoutLarge() {
		buttonLayoutLarge.setDisable(true);
		buttonLayoutMedium.setDisable(false);
		buttonLayoutSmall.setDisable(false);

		clear();

		gridPane.getChildren().add(tabPaneEditor);
		gridPane.getChildren().add(tabPaneScene);
		gridPane.getChildren().add(tabPaneMarker);
		gridPane.getChildren().add(tabPaneResult);

		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneScene.getTabs().add(tabScene);
		tabPaneMarker.getTabs().add(tabMarker);
		tabPaneResult.getTabs().add(tabResult);

		GridPane.setConstraints(tabPaneEditor, 1, 0, 1, 1);
		GridPane.setConstraints(tabPaneScene, 2, 0, 1, 1);
		GridPane.setConstraints(tabPaneMarker, 1, 1, 1, 1);
		GridPane.setConstraints(tabPaneResult, 2, 1, 1, 1);
	}

	@FXML
	public void layoutMedium() {
		buttonLayoutLarge.setDisable(false);
		buttonLayoutMedium.setDisable(true);
		buttonLayoutSmall.setDisable(false);

		clear();

		gridPane.getChildren().add(tabPaneEditor);
		gridPane.getChildren().add(tabPaneMarker);

		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneEditor.getTabs().add(tabScene);
		tabPaneMarker.getTabs().add(tabMarker);
		tabPaneMarker.getTabs().add(tabResult);

		GridPane.setConstraints(tabPaneEditor, 1, 0, 2, 1);
		GridPane.setConstraints(tabPaneMarker, 1, 1, 2, 1);
	}

	@FXML
	public void layoutSmall() {
		buttonLayoutLarge.setDisable(false);
		buttonLayoutMedium.setDisable(false);
		buttonLayoutSmall.setDisable(true);

		clear();

		gridPane.getChildren().add(tabPaneEditor);

		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneEditor.getTabs().add(tabScene);
		tabPaneEditor.getTabs().add(tabMarker);
		tabPaneEditor.getTabs().add(tabResult);

		GridPane.setConstraints(tabPaneEditor, 1, 0, 2, 2);
	}

	private void clear() {
		// clear tab panes
		tabPaneEditor.getTabs().clear();
		tabPaneScene.getTabs().clear();
		tabPaneMarker.getTabs().clear();
		tabPaneResult.getTabs().clear();

		// remove tab panes
		gridPane.getChildren().remove(tabPaneEditor);
		gridPane.getChildren().remove(tabPaneScene);
		gridPane.getChildren().remove(tabPaneMarker);
		gridPane.getChildren().remove(tabPaneResult);
	}

	public void cleanup() throws InterruptedException {
		simulator.setRunning(false);
		animation.setRunning(false);

		simulator.join();
		animation.join();

		// unsubscribe embedded controllers
		EventBus.getInstance().unsubscribe(embeddedExplorerController);
		EventBus.getInstance().unsubscribe(embeddedEditorController);
		EventBus.getInstance().unsubscribe(embeddedSceneController);
		EventBus.getInstance().unsubscribe(embeddedMarkerController);
		EventBus.getInstance().unsubscribe(embeddedResultController);
		EventBus.getInstance().unsubscribe(embeddedAttributeController);
	}

	public void setElement(Scenario scenario) {
		DefinitionComponent component = (DefinitionComponent) scenario.getParent();

		// Create simulator and animator
		simulator = new Simulator(component, scenario, Main.SESSION_PATH);
		animation = new AnimationThread(simulator, sStep, spIdleTime);

		// Propagate simulator
		embeddedExplorerController.setSimulator(simulator);
		embeddedEditorController.setSimulator(simulator);
		embeddedSceneController.setSimulator(simulator);
		embeddedMarkerController.setSimulator(simulator);
		embeddedResultController.setSimulator(simulator);
		embeddedAttributeController.setSimulator(simulator);

		// init tree view
		embeddedExplorerController.setElement(component);
		
		// Start simulator and animator
		simulator.start();
		animation.start();

		// bind slider to step property
		sStep.maxProperty().bind(simulator.stepProperty());

		// bind backward and forward buttons on step count and executing
		// property
		BooleanBinding atStart = Bindings.equal(sStep.valueProperty(), 0);
		BooleanBinding atEnd = Bindings.equal(sStep.valueProperty(), simulator.stepProperty());

		bBackward.disableProperty().bind(atStart);
		bPlay.managedProperty().bind(animation.pausedProperty());
		bPlay.visibleProperty().bind(animation.pausedProperty());
		bPause.managedProperty().bind(Bindings.not(animation.pausedProperty()));
		bPause.visibleProperty().bind(Bindings.not(animation.pausedProperty()));
		bForward.disableProperty().bind(atEnd);

		sStep.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (oldValue != newValue) {
							EventBus.getInstance().publish(new SimulationStepEvent(newValue.intValue()));
						}
					}
				});
			}
		});
	}

	@FXML
	private void handlePlay() {
		animation.setPaused(false);
	}

	@FXML
	private void handlePause() {
		animation.setPaused(true);
	}

	@FXML
	private void handleForward() {
		sStep.setValue(sStep.getValue() + 1);
	}

	@FXML
	private void handleBackward() {
		sStep.setValue(sStep.getValue() - 1);
	}
}
