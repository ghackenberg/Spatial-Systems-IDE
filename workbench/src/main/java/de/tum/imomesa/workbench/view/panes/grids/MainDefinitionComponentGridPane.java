package de.tum.imomesa.workbench.view.panes.grids;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.view.panes.tabs.MainDefinitionComponentBottomLeftTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainDefinitionComponentBottomRightTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainDefinitionComponentTopLeftTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainDefinitionComponentTopRightTabPane;

public class MainDefinitionComponentGridPane extends AbstractGridPane {

	public MainDefinitionComponentGridPane(DefinitionComponent definition) {
		super(new double[] { 50, 50 }, new double[] { 70, 30 });
		add(new MainDefinitionComponentTopLeftTabPane(definition), 0, 0);
		add(new MainDefinitionComponentTopRightTabPane(definition), 1, 0);
		add(new MainDefinitionComponentBottomLeftTabPane(definition), 0, 1);
		add(new MainDefinitionComponentBottomRightTabPane(definition), 1, 1);
	}

}
