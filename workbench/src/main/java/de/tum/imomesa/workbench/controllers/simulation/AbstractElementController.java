package de.tum.imomesa.workbench.controllers.simulation;

import java.util.List;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public abstract class AbstractElementController<T extends Element> implements EventHandler {

	@FXML
	protected BorderPane borderPane;
	@FXML
	protected ToolBar topToolBar;

	@FXML
	public void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				EventBus.getInstance().subscribe(AbstractElementController.this);
			}
		});
	}

	protected Simulator simulator;
	protected List<Element> extendedContext;
	protected List<Element> context;
	protected T element;
	protected Component<?> component;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public void setExtendedContext(List<Element> extendedContext) {
		this.extendedContext = extendedContext;
	}

	public void setContext(List<Element> context) {
		this.context = context;
	}

	public void setElement(T element) {
		this.element = element;
	}

	public void setComponent(Component<?> component) {
		this.component = component;
	}

	public abstract void render();
	
	public abstract void handle(SimulationStepEvent event);

}
