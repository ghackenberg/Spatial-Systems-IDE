package de.tum.imomesa.workbench.controllers.simulation;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.simulator.Simulator;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import de.tum.imomesa.workbench.controllers.AbstractExplorerController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class ExplorerController extends AbstractExplorerController implements EventHandler {

	@FXML
	private BorderPane borderPane;

	@FXML
	private ToolBar topToolBar;
	@FXML
	private ToolBar leftToolBar;

	@FXML
	private TextField searchTextField;

	@FXML
	private ToggleButton ports;
	@FXML
	private ToggleButton properties;
	@FXML
	private ToggleButton scenarios;
	@FXML
	private ToggleButton monitors;
	@FXML
	private ToggleButton components;
	@FXML
	private ToggleButton behaviors;

	@FXML
	private TreeView<Object> treeView;

	private Simulator simulator;

	@FXML
	public void initialize() {
		EventBus.getInstance().subscribe(this);

		borderPane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				borderPane.setLeft(null);
				borderPane.setBottom(null);

				if (newValue.intValue() > 300) {
					borderPane.setLeft(leftToolBar);
					borderPane.setBottom(null);
					leftToolBar.setOrientation(Orientation.VERTICAL);
				} else {
					borderPane.setLeft(null);
					borderPane.setBottom(leftToolBar);
					leftToolBar.setOrientation(Orientation.HORIZONTAL);
				}
			}
		});

		searchTextField.prefWidthProperty().bind(topToolBar.widthProperty().subtract(16));
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;

		treeView.setRoot(new TreeItem<Object>("Simulation", ImageHelper.getImageAsIcon("images/simulation.png")));
	}

	public void handle(SimulationStepEvent event) {
		simulator.getMemory().getProxy(event.getStep());
	}

}