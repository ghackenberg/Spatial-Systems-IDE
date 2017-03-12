package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.utilities.managers.StorageManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ChangeController {

	@FXML private TableView<Change> table;
	
	public void initialize() {		
		table.setItems(StorageManager.getInstance().getChanges());
	}
}
