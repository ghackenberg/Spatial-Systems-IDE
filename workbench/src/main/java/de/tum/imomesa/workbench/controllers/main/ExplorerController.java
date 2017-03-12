package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.controllers.AbstractExplorerController;
import de.tum.imomesa.workbench.explorers.Configuration;
import de.tum.imomesa.workbench.explorers.CustomTreeCell;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;
import de.tum.imomesa.workbench.explorers.listeners.ElementSelectionListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class ExplorerController extends AbstractExplorerController {

	@FXML
	private BorderPane borderPane;

	@FXML
	private ToolBar topToolBar;
	@FXML
	private ToolBar leftToolBar;

	@FXML
	private TextField searchTextField;

	@FXML
	private ToggleButton requirements;
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
	private ToggleButton channels;
	@FXML
	private ToggleButton behaviors;
	@FXML
	private ToggleButton parts;
	@FXML
	private ToggleButton transforms;

	@FXML
	private TreeView<Element> tree;

	private Configuration configuration = new Configuration();

	/**
	 * This method is called automatically when Controller is initialized It
	 * sets all the necessary information in the controller
	 */
	@FXML
	public void initialize() {
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

		// Bind configuration parameters
		configuration.requirementsProperty().bind(requirements.selectedProperty());
		configuration.portsProperty().bind(ports.selectedProperty());
		configuration.propertiesProperty().bind(properties.selectedProperty());
		configuration.scenariosProperty().bind(scenarios.selectedProperty());
		configuration.monitorsProperty().bind(monitors.selectedProperty());
		configuration.componentsProperty().bind(components.selectedProperty());
		configuration.channelsProperty().bind(channels.selectedProperty());
		configuration.behaviorsProperty().bind(behaviors.selectedProperty());
		configuration.partsProperty().bind(parts.selectedProperty());
		configuration.transformsProperty().bind(transforms.selectedProperty());

		// get workspace
		Workspace workspace = StorageManager.getInstance().getWorkspace();

		// set up workspace
		TreeItem<Element> root = TreeItemBuilder.getInstance().transform(workspace, configuration, true);
		tree.setRoot(root);

		// set tree view not editable and add cell factory for context menu
		tree.setEditable(false);
		tree.setCellFactory(new Callback<TreeView<Element>, TreeCell<Element>>() {
			@Override
			public TreeCell<Element> call(TreeView<Element> p) {
				return new CustomTreeCell();
			}
		});

		// add selection listener for other parts to react on it
		tree.getSelectionModel().selectedItemProperty().addListener(new ElementSelectionListener());

		// select root
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tree.getSelectionModel().select(root);
			}
		});
	}

	@FXML
	public void handleSearchClick() {
		new Alert(AlertType.INFORMATION, "Search not implemented yet!", ButtonType.OK).showAndWait();
	}

}
