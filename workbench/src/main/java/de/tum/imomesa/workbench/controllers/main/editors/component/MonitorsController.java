package de.tum.imomesa.workbench.controllers.main.editors.component;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class MonitorsController implements AbstractEditorChildController<DefinitionComponent> {

	@FXML
	private TableView<Monitor> tableView;

	@Override
	public void setRootElement(DefinitionComponent e) {
		tableView.setItems(e.monitorsProperty());
	}
	
	@Override
	public void setSelectedElement(Element element) {
		
	}

}
