package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.attributes.AttributesEditorBuilder;
import de.tum.imomesa.workbench.commons.events.SelectionDiagramEvent;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.controllers.AbstractAttributeController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class AttributeController extends AbstractAttributeController implements EventHandler {
	
	@FXML private ScrollPane scrollPane;
	
	public void initialize() {
		EventBus.getInstance().subscribe(this);
	}
	
	public void handle(SelectionExplorerEvent event) {
		showAttributesOfElement(event.getSelected());
	}
	
	public void handle(SelectionDiagramEvent event) {
		showAttributesOfElement(event.getSelected());
	}
	
	private void showAttributesOfElement(Element element) {
		if (element != null) {
			scrollPane.setContent(AttributesEditorBuilder.getAttributes(element));
		}
		else {
			scrollPane.setContent(null);
		}
	}
}
