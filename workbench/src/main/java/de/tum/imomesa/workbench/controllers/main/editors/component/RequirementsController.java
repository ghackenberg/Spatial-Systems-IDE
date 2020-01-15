package de.tum.imomesa.workbench.controllers.main.editors.component;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class RequirementsController implements AbstractEditorChildController<DefinitionComponent> {

	@FXML
	private TableView<Requirement> tableView;
	
	@Override
	public void setRootElement(DefinitionComponent e) {
		tableView.setItems(e.requirementsProperty());
	}
	
	@Override
	public void setSelectedElement(Element element) {
		
	}

}
