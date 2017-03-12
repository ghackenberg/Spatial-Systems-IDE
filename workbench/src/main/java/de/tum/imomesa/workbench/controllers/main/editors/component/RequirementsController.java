package de.tum.imomesa.workbench.controllers.main.editors.component;

import de.tum.imomesa.model.Requirement;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class RequirementsController implements AbstractElementController<DefinitionComponent> {

	@FXML
	private TableView<Requirement> tableView;
	
	@Override
	public void setElement(DefinitionComponent e) {
		tableView.setItems(e.requirementsProperty());
	}

}
