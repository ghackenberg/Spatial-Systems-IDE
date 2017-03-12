package de.tum.imomesa.workbench.attributes.listeners;

import java.util.Optional;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.ports.DefinitionPort;
import de.tum.imomesa.model.ports.ReferencePort;
import de.tum.imomesa.utilities.managers.StorageManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

public class ChangeListenerComponentProxyTemplate implements ChangeListener<DefinitionComponent> {
	
	// attributes
	private ReferenceComponent data; 
	private ComboBox<DefinitionComponent> cbTemplate;
	private boolean abortChanging = false;
	
	// constructor
	public ChangeListenerComponentProxyTemplate(ReferenceComponent c, ComboBox<DefinitionComponent> cbTemplate) {
		this.data = c;
		this.cbTemplate = cbTemplate;
	}
	
	@Override
	public void changed(ObservableValue<? extends DefinitionComponent> observable, DefinitionComponent oldValue, DefinitionComponent newValue) {
		// abortChanging: means the value is set back to the old value -> so nothing shall be done
		if(abortChanging == true) {
			abortChanging = false;
			return;
		}

		// ask if changing is wanted by user
		if(oldValue != null) {
			// show warning
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Changing of template");
			alert.setHeaderText(null);
			alert.setContentText("This step will override all ports. Do you want to continue?");
		
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			    // ... user chose OK: continue
			} else {
			    // ... user chose CANCEL or closed the dialog
				abortChanging = true;
				
				// select old value when finished
				Platform.runLater(() -> {
					cbTemplate.getSelectionModel().select(oldValue);
				});
				return;
			}
		}
		
		// remove proxies
		while (!data.getPorts().isEmpty()) {
			StorageManager.getInstance().releaseElement(data.getPorts().remove(0));
		}
		// set template
		data.setTemplate(newValue);
		// add proxies
		for (DefinitionPort port : newValue.getPorts()) {
			data.getPorts().add(new ReferencePort(data, port));
		}
	}
}
