package de.tum.imomesa.workbench.controllers.main.editors.project;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class TemplatesController implements AbstractEditorChildController<Project> {

	@FXML
	private TableView<DefinitionComponent> tableView;

	@Override
	public void setRootElement(Project e) {
		tableView.setItems(e.templatesProperty());
	}
	
	@Override
	public void setSelectedElement(Element element) {
		
	}

}
