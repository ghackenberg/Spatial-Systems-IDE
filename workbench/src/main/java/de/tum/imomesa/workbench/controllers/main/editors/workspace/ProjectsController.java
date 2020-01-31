package de.tum.imomesa.workbench.controllers.main.editors.workspace;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ProjectsController implements AbstractEditorChildController<Workspace> {

	@FXML
	private TableView<Project> tableView;

	@Override
	public void setRootElement(Workspace e) {
		tableView.setItems(e.projectsProperty());
	}

	@Override
	public void setSelectedElement(Element element) {

	}

}
