package de.tum.imomesa.workbench.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

public class MainController {

	@FXML
	private Button buttonLayoutOne;
	@FXML
	private Button buttonLayoutTwo;
	@FXML
	private Button buttonLayoutThree;
	
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
	private TabPane tabPaneChange;
	@FXML
	private TabPane tabPaneAttribute;
	
	//private Tab tabExplorer;
	private Tab tabEditor;
	private Tab tabScene;
	private Tab tabMarker;
	private Tab tabChange;
	//private Tab tabAttribute;
	
	@FXML
	public void initialize() {
		//tabExplorer = tabPaneExplorer.getTabs().get(0);
		tabEditor = tabPaneEditor.getTabs().get(0);
		tabScene = tabPaneScene.getTabs().get(0);
		tabMarker = tabPaneMarker.getTabs().get(0);
		tabChange = tabPaneChange.getTabs().get(0);
		//tabAttribute = tabPaneAttribute.getTabs().get(0);
	}
	
	@FXML
	public void layoutOne() {
		// update buttons
		buttonLayoutOne.setDisable(true);
		buttonLayoutTwo.setDisable(false);
		buttonLayoutThree.setDisable(false);
		
		// clear layout
		clear();
		
		// add tab panes
		gridPane.getChildren().add(tabPaneEditor);
		gridPane.getChildren().add(tabPaneScene);
		gridPane.getChildren().add(tabPaneMarker);
		gridPane.getChildren().add(tabPaneChange);
		
		// add tabs
		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneScene.getTabs().add(tabScene);
		tabPaneMarker.getTabs().add(tabMarker);
		tabPaneChange.getTabs().add(tabChange);
		
		// set contraints
		GridPane.setConstraints(tabPaneEditor, 1, 0, 1, 1);
		GridPane.setConstraints(tabPaneScene, 2, 0, 1, 1);
		GridPane.setConstraints(tabPaneMarker, 1, 1, 1, 1);
		GridPane.setConstraints(tabPaneChange, 2, 1, 1, 1);
	}
	@FXML
	public void layoutTwo() {
		// update buttons
		buttonLayoutOne.setDisable(false);
		buttonLayoutTwo.setDisable(true);
		buttonLayoutThree.setDisable(false);
		
		// clear layout
		clear();
		
		// add tab panes
		gridPane.getChildren().add(tabPaneEditor);
		gridPane.getChildren().add(tabPaneMarker);
		
		// add tabs
		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneEditor.getTabs().add(tabScene);
		tabPaneMarker.getTabs().add(tabMarker);
		tabPaneMarker.getTabs().add(tabChange);
		
		// set contraints
		GridPane.setConstraints(tabPaneEditor, 1, 0, 2, 1);
		GridPane.setConstraints(tabPaneMarker, 1, 1, 2, 1);
	}
	@FXML
	public void layoutThree() {
		// update buttons
		buttonLayoutOne.setDisable(false);
		buttonLayoutTwo.setDisable(false);
		buttonLayoutThree.setDisable(true);
		
		// clear layout
		clear();
		
		// add tab panes
		gridPane.getChildren().add(tabPaneEditor);
		
		// add tabs
		tabPaneEditor.getTabs().add(tabEditor);
		tabPaneEditor.getTabs().add(tabScene);
		tabPaneEditor.getTabs().add(tabMarker);
		tabPaneEditor.getTabs().add(tabChange);
		
		// set constraints
		GridPane.setConstraints(tabPaneEditor, 1, 0, 2, 2);
	}
	
	private void clear() {
		// clear tab panes
		tabPaneEditor.getTabs().clear();
		tabPaneScene.getTabs().clear();
		tabPaneMarker.getTabs().clear();
		tabPaneChange.getTabs().clear();
		
		// remove tab panes
		gridPane.getChildren().remove(tabPaneEditor);
		gridPane.getChildren().remove(tabPaneScene);
		gridPane.getChildren().remove(tabPaneMarker);
		gridPane.getChildren().remove(tabPaneChange);
	}
	
}
