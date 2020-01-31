package de.tum.imomesa.workbench.controllers.main.editors;

import java.io.IOException;

import de.tum.imomesa.checker.manager.SyntacticMarkerManager;
import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.executables.scenarios.Step;
import de.tum.imomesa.model.executables.scenarios.Transition;
import de.tum.imomesa.workbench.Main;
import de.tum.imomesa.workbench.commons.events.SimulationEndEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStartEvent;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import de.tum.imomesa.workbench.controllers.SimulationController;
import de.tum.imomesa.workbench.diagrams.ScenarioDiagram;
import de.tum.imomesa.workbench.diagrams.ScenarioDiagramBehavior;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ScenarioController implements AbstractEditorChildController<Scenario> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonCreateStep;
	@FXML
	private Button buttonSetInitialStep;
	@FXML
	private Button buttonSetFinalStep;
	@FXML
	private Button buttonRemoveStep;
	@FXML
	private Button buttonRemoveTransition;

	private ScenarioDiagram diagram;

	@FXML
	public void initialize() {
		buttonSetInitialStep.setDisable(true);
		buttonSetFinalStep.setDisable(true);
		buttonRemoveStep.setDisable(true);
		buttonRemoveTransition.setDisable(true);
	}

	@Override
	public void setRootElement(Scenario element) {
		pane.getChildren().clear();

		final ChangeListener<Element> listener = (observable, oldValue, newValue) -> {
			buttonSetInitialStep.setDisable(!checkType(Step.class) || checkObject(element.getInitialLabel()));
			buttonSetFinalStep.setDisable(!checkType(Step.class) || checkObject(element.getFinalLabel()));
			buttonRemoveStep.setDisable(!checkType(Step.class));
			buttonRemoveTransition.setDisable(!checkType(Transition.class));
		};

		diagram = new ScenarioDiagram(pane, element, new ScenarioDiagramBehavior(), true);
		diagram.clickedElementProperty().addListener(listener);
		diagram.pressedElementProperty().addListener(listener);

		// TODO Remove listener when not needed anymore!
		element.initialLabelProperty().addListener(listener);
		// TODO Remove listener when not needed anymore!
		element.finalLabelProperty().addListener(listener);
	}

	private <T extends Element> boolean checkObject(T object) {
		return object != null && (diagram.getClickedElement() == object || diagram.getPressedElement() == object);
	}

	private <T extends Element> boolean checkType(Class<T> type) {
		return checkType(diagram.getClickedElement(), type) || checkType(diagram.getPressedElement(), type);
	}

	private <T extends Element> boolean checkType(Element element, Class<T> type) {
		return element != null && type.isAssignableFrom(element.getClass());
	}

	@Override
	public void setSelectedElement(Element element) {
		Step label = element.getFirstAncestorByType(Step.class);
		Transition transition = element.getFirstAncestorByType(Transition.class);
		Scenario executable = element.getFirstAncestorByType(Scenario.class);

		if (label != null) {
			diagram.setClickedElement(label);
		} else if (transition != null) {
			diagram.setClickedElement(transition);
		} else if (executable != null) {
			diagram.setClickedElement(executable);
		}
	}

	@FXML
	public void createStep() {
		Step step = new Step();
		step.setName("New step");
		step.setParent(diagram.getElement());
		step.accept(new SyntacticCheckVisitor());

		diagram.getElement().getLabels().add(step);
		diagram.setClickedElement(step);
	}

	@FXML
	public void setInitialStep() {
		diagram.getElement().setInitialLabel((Step) diagram.getClickedElement());
	}

	@FXML
	public void setFinalStep() {
		diagram.getElement().setFinalLabel((Step) diagram.getClickedElement());
	}

	@FXML
	public void removeStep() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

	@FXML
	public void removeTransition() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

	@FXML
	private void startSimulation() {

		// check first, if errors in model exist
		if (SyntacticMarkerManager.getInstance().containsError() == true) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error loading simulation");
			alert.setHeaderText("The model still contains errors.");
			alert.setContentText("The simulation can not be started.");
			alert.showAndWait();
			return;
		}

		// open simulation in new window
		try {
			Scenario scenario = diagram.getElement();

			// publish event on EventBus
			DefinitionComponent component = scenario.getFirstAncestorByType(DefinitionComponent.class);
			EventBus.getInstance().publish(new SimulationStartEvent(component, scenario));

			// load new view
			FXMLLoader loader = new FXMLLoader(ScenarioController.class.getResource("/views/Simulation.fxml"));
			Parent root = loader.load();
			SimulationController controller = (SimulationController) loader.getController();
			controller.setElement(scenario);

			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(pane.getScene().getWindow());
			/* stage.setIconified(true); */
			stage.getIcons().add(new Image("images/icon.png"));
			stage.setTitle(Main.NAME + " - Simulation");
			stage.setScene(new Scene(root, 1000, 1000));
			stage.setMaximized(true);
			stage.show();

			// catch closing action
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					try {
						// close simulation thread
						controller.cleanup();
						// publish event on EventBus
						EventBus.getInstance().publish(new SimulationEndEvent());
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
