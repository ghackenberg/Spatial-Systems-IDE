package de.tum.imomesa.workbench.view.panes.borders;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Project;
import de.tum.imomesa.model.Workspace;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.view.panes.grids.MainDefinitionComponentGridPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainProjectTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainReferenceComponentTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainWelcomeTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainWorkspaceTabPane;

public class MainObjectBorderPane extends AbstractBorderPane implements EventHandler {

	public MainObjectBorderPane() {
		setCenter(new MainWelcomeTabPane());
		EventBus.getInstance().subscribe(this);
	}

	public void handle(SelectionExplorerEvent event) {
		if (event.getSelected() instanceof Workspace) {
			show((Workspace) event.getSelected());
		} else if (event.getSelected() instanceof Project) {
			show((Project) event.getSelected());
		} else if (event.getSelected() instanceof DefinitionComponent) {
			show((DefinitionComponent) event.getSelected());
		} else if (event.getSelected() instanceof ReferenceComponent) {
			show((ReferenceComponent) event.getSelected());
		} else if (event.getSelected() == null) {
			show();
		} else {
			throw new IllegalStateException();
		}
	}

	protected void show() {
		setCenter(new MainWelcomeTabPane());
	}

	protected void show(Workspace workspace) {
		setCenter(new MainWorkspaceTabPane(workspace));
	}

	protected void show(Project project) {
		setCenter(new MainProjectTabPane(project));
	}

	protected void show(DefinitionComponent definition) {
		setCenter(new MainDefinitionComponentGridPane(definition));
	}

	protected void show(ReferenceComponent reference) {
		setCenter(new MainReferenceComponentTabPane(reference));
	}

}
