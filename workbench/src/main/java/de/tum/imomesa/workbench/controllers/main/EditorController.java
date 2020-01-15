package de.tum.imomesa.workbench.controllers.main;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.controllers.AbstractEditorController;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;

public class EditorController extends AbstractEditorController implements EventHandler {

	public EditorController() {
		super("/views/main/editors/");
	}

	public void initialize() {
		EventBus.getInstance().subscribe(this);
	}

	public void handle(SelectionExplorerEvent event) {
		// obtain controller
		AbstractEditorChildController<Element> controller = loadEditor(event.getSelected());
		// check controller
		if (controller != null) {
			// set selected element
			controller.setSelectedElement(event.getSelected());
		}
	}

}
