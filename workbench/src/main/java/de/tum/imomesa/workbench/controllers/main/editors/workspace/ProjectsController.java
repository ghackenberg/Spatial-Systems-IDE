package de.tum.imomesa.workbench.controllers.main.editors.workspace;

import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ProjectsController implements AbstractElementController<Workspace> {

	@FXML
	private TableView<Project> tableView;

	@Override
	public void setElement(Workspace e) {
		tableView.setItems(e.projectsProperty());
	}

}
