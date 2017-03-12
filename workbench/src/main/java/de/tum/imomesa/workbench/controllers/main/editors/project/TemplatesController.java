package de.tum.imomesa.workbench.controllers.main.editors.project;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class TemplatesController implements AbstractElementController<Project> {

	@FXML
	private TableView<DefinitionComponent> tableView;

	@Override
	public void setElement(Project e) {
		tableView.setItems(e.templatesProperty());
	}

}
