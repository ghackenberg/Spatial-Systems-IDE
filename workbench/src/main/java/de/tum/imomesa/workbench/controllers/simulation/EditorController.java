package de.tum.imomesa.workbench.controllers.simulation;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.AbstractEditorController;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class EditorController extends AbstractEditorController implements EventHandler {

	public EditorController() {
		super("/views/simulation/editors/");
	}

	@FXML
	public void initialize() {
		EventBus.getInstance().subscribe(this);
	}

	private Simulator simulator;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	private SimulationStepEvent lastEvent = new SimulationStepEvent(0);

	public void handle(SimulationStepEvent event) {
		lastEvent = event;
	}

	private AbstractElementController<Element> lastController;

	public void handle(SimulationSelectionEvent event) {
		// check controller
		if (lastController != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// unsubscribe controller
					EventBus.getInstance().unsubscribe(lastController);
				}
			});
		}
		// obtain controller
		lastController = loadEditor(event.getElement());
		// check controller
		if (lastController != null) {
			// set simulator
			lastController.setSimulator(simulator);
			// set extended context
			lastController.setExtendedContext(event.getExtendedContext());
			// set context
			lastController.setContext(event.getContext());
			// set element
			lastController.setElement(event.getElement());
			// set component
			lastController.setComponent(event.getComponent());
			// render
			lastController.render();
			// handle
			lastController.handle(lastEvent);
		}
	}

}
