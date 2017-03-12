package de.tum.imomesa.workbench.explorers.handlers;

import java.util.Optional;

import de.tum.imomesa.checker.visitor.SyntacticCheckRemoverVisitor;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.utilities.managers.StorageManager;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

public class RemoveElementHandler implements EventHandler<ActionEvent> {
	
	private Element object;
	
	public RemoveElementHandler(Element object) {
		this.object = object;
	}
	
	@Override
	public void handle(ActionEvent event) {
		if(confirmDeletion(object) == true) {
			object.accept(new SyntacticCheckRemoverVisitor());
			StorageManager.getInstance().releaseElement(object);
		}
	}
	
	private boolean confirmDeletion(Element e) {
		ImageView icon = ImageHelper.getImageAsIcon("icons/warning.png");
		icon.setFitHeight(30);
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deleting object");
		alert.setHeaderText(null);
		alert.setGraphic(icon);
		alert.setContentText("Do you really want to delete this object:\n" + e.getClass().getSimpleName() + " \"" + e.toString() + "\"");
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			return true;
		}
		else {
			return false;
		}
	}

}
