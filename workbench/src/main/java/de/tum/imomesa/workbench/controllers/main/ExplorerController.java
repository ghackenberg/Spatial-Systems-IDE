package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.controllers.AbstractExplorerController;
import de.tum.imomesa.workbench.explorers.CustomTreeCell;
import de.tum.imomesa.workbench.explorers.TreeItemBuilder;
import de.tum.imomesa.workbench.explorers.listeners.ElementSelectionListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
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
	private TextField searchTextField;

	@FXML
	private TreeView<Element> tree;

	/**
	 * This method is called automatically when Controller is initialized It sets all the necessary information in the controller
	 */
	@FXML
	public void initialize() {
		searchTextField.prefWidthProperty().bind(topToolBar.widthProperty().subtract(16));

		// get workspace
		Workspace workspace = StorageManager.getInstance().getWorkspace();

		// set up workspace
		TreeItem<Element> root = TreeItemBuilder.getInstance().process(workspace);
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
