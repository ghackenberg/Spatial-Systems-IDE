package de.tum.imomesa.workbench.explorers.dialogs;

import de.tum.imomesa.model.Element;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public abstract class SimpleAddDialog<T extends Element> extends Dialog<T> {

	@SafeVarargs
	public SimpleAddDialog(String title, String label, T... options) {
		setTitle(title);
        setHeaderText(null);
        setContentText(null);
	    setResizable(true);
	    
		ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(options));
	    comboBox.setValue(options[0]);
		comboBox.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				comboBox.requestFocus();
			}
		});
		
		GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);
		content.add(new Label(label), 0, 0);
		content.add(comboBox, 1, 0);

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().setContent(content);
	    
	    setResultConverter(dialogButton -> {
	        if (dialogButton == ButtonType.OK) {
	        	return comboBox.getValue();
	        }
	        else {
	        	return null;
	        }
	    });
	}

}
