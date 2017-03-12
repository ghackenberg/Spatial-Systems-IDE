package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.model.volumes.Volume;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

public class VolumeAddDialog extends Dialog<Volume> {

	// attributes
    private static ObservableList<String> options = FXCollections.observableArrayList("Atomic Volume", "Composite Volume");
    private ObservableList<Volume> optionsAtomic = FXCollections.observableArrayList(new BoxVolume(), new CylinderVolume(), new SphereVolume() );
    
    // GUI elements
    private ToggleGroup group = new ToggleGroup();
    
    // Constructor
	public VolumeAddDialog() {
		// set title and header
		setTitle("Create Volume");
		setHeaderText(null);
		
		// button types
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// layout a custom GridPane containing the input fields and labels
	    GridPane content = new GridPane();
	    content.setHgap(10);
	    content.setVgap(10);
	    content.setPadding(new Insets(5));
	    
	    // Content
	    content.add(new Label("Choose the type of volume"), 0, 0);
	    ComboBox<String> comboBox = new ComboBox<>(options);
	    content.add(comboBox, 1, 0);
	    // separator over two lines
	    content.add(new Separator(), 0, 1, 2, 1);
	    
	    // action on change of combo box
	    comboBox.setOnMousePressed(new EventHandler<MouseEvent>(){
	        @Override
	        public void handle(MouseEvent event) {
	            comboBox.requestFocus();
	        }
	    });
	    comboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// atomic volume
				if(comboBox.getValue().equals(options.get(0))) {
					addRadioButtons(content, optionsAtomic);
				}
				// composite volume
				if(comboBox.getValue().equals(options.get(1))) {
					addRadioButtons(content, null);
				}
			}
		});
	    
	    // add content
	    getDialogPane().setContent(content);
	    addRadioButtons(content, optionsAtomic);
	    
	    // set options
	    comboBox.setValue(options.get(0));
	    setResizable(true);

	    // result converter
	    setResultConverter(dialogButton -> {
	        if (dialogButton == ButtonType.OK) {
	        	// if atomic -> selected toggle
	        	if(comboBox.getValue().equals(options.get(0))) {
	        		return optionsAtomic.get(group.getToggles().indexOf(group.getSelectedToggle()));
	        	}
	        	
	        	// if composite -> new CompositeVolume
	        	else {
	        		return new CompositeVolume();
	        	}
	        }
	        return null;
	    });
	}
	
	private void addRadioButtons(GridPane content, ObservableList<Volume> items) {
		// remove existing toggles
		ObservableList<Toggle> l = group.getToggles();
		for(Toggle rb : l) {
			content.getChildren().remove((RadioButton)rb);
		}
		
		if(items == null) {
			// nothing to show
			return;
		}
		
		// create toggle group
		group = new ToggleGroup();
		
		// indicator for first run of loop to set selected item
		boolean firstRun = true;
		int index = 2;
		
		for(Volume v : items) {
			// create radiobutton and add to group
			RadioButton rb = new RadioButton(v.toString());
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