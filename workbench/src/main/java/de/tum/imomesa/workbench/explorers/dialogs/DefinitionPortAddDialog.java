package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.ports.DataPort;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.ElectricEnergyPort;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.GenericEnergyPort;
import de.tum.imomesa.model.ports.GenericPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.Port;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class DefinitionPortAddDialog extends Dialog<DefinitionPort> {

    private static ObservableList<String> options = FXCollections.observableArrayList("Material port", "Energy port", "Data port", "Generic port");
	
    private ObservableList<DefinitionPort> optionsMaterialPort = FXCollections.observableArrayList(new InteractionMaterialPort(), new EntryLifeMaterialPort(), new ExitLifeMaterialPort());
    private ObservableList<DefinitionPort> optionsEnergyPort = FXCollections.observableArrayList(new ElectricEnergyPort(), new KinematicEnergyPort(), new GenericEnergyPort());
    private ObservableList<DefinitionPort> optionsDataPort = FXCollections.observableArrayList(new DataPort());
    private ObservableList<DefinitionPort> optionsEventPort = FXCollections.observableArrayList(new GenericPort());
	
    private ToggleGroup group = new ToggleGroup();
    
    private ObservableList<DefinitionPort> selectedList;
    
	public DefinitionPortAddDialog() {
		// set title and header
		setTitle("Add port");
		setHeaderText(null);
		
		// button types
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// layout a custom GridPane containing the input fields and labels
	    GridPane content = new GridPane();
	    content.setHgap(10);
	    content.setVgap(10);
	    
	    // Label
	    content.add(new Label("Choose the type of port"), 0, 0);
	    
	    // ComboBox
	    ComboBox<String> comboBox = new ComboBox<>(options);
	    
	    content.add(comboBox, 1, 0);
	    // separator over two lines
	    content.add(new Separator(), 0, 1, 2, 1);
	    
	    // action on change of combo box
	    comboBox.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				comboBox.requestFocus();
			}
		});
	    comboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// material port
				if(comboBox.getValue().equals(options.get(0))) {
					addRadioButtons(content, optionsMaterialPort);
				}
				// energy port
				else if(comboBox.getValue().equals(options.get(1))) {
					addRadioButtons(content, optionsEnergyPort);
				}
				// data port
				else if(comboBox.getValue().equals(options.get(2))) {
					addRadioButtons(content, optionsDataPort);
				}
				// event port
				else if(comboBox.getValue().equals(options.get(3))) {
					addRadioButtons(content, optionsEventPort);
				}
				else {
					throw new IllegalStateException();
				}
			}
		});

	    // add content
	    getDialogPane().setContent(content);
	    addRadioButtons(content, optionsMaterialPort);
	    
	    // set options
	    comboBox.setValue(options.get(0));
	    setResizable(true);

	    // result converter
	    setResultConverter(dialogButton -> {
	        if (dialogButton == ButtonType.OK) {
	    		// get selected item
	    		int selectedRadioButton = group.getToggles().indexOf((RadioButton)(group.getSelectedToggle()));
	    		
	    		// if no item was selected, return NULL-Pointer
	    		if(selectedRadioButton < 0 || selectedRadioButton >= selectedList.size() ) {
	    			return null;
	    		}
	    		
	    		// get list object
	    		return selectedList.get(selectedRadioButton);
	        }
	        return null;
	    });

	}
	
	private void addRadioButtons(GridPane content, ObservableList<DefinitionPort> items) {
		// save selected list
		selectedList = items;
		
		ObservableList<Toggle> l = group.getToggles();
		for(Toggle rb : l) {
			// remove
			content.getChildren().remove((RadioButton)rb);
		}
		
		// create toggle group
		group = new ToggleGroup();
		
		// indicator for first run of loop to set selected item
		boolean firstRun = true;
		int index = 2;
		
		for(Port p : items) {
			// create radiobutton and add to group
			RadioButton rb = new RadioButton(p.getClass().getSimpleName());
			rb.setToggleGroup(group);
			if(firstRun == true) {
				// if first run, set item as selected
				rb.setSelected(true);
				firstRun = false;
			}
			// add to content
			content.add(rb, 0, index, 2, 1);
			index++;
		}
	}
}
