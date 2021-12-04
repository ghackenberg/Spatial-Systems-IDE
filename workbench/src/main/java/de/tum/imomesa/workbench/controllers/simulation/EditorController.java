package de.tum.imomesa.workbench.controllers.simulation;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationSelectionEvent;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.AbstractEditorController;
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

	public void handle(SimulationStepEvent event) {

	}

	public void handle(SimulationSelectionEvent event) {

	}

}
