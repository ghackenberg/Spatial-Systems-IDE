package de.tum.imomesa.workbench.view.panes.grids;

import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.workbench.view.panes.tabs.MainComponentBottomLeftTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainComponentBottomRightTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainComponentTopLeftTabPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainComponentTopRightTabPane;

public class MainDefinitionComponentGridPane extends AbstractGridPane {

	public MainDefinitionComponentGridPane(DefinitionComponent definition) {
		super(new double[] { 50, 50 }, new double[] { 70, 30 });
		add(new MainComponentTopLeftTabPane(definition), 0, 0);
		add(new MainComponentTopRightTabPane(definition), 1, 0);
		add(new MainComponentBottomLeftTabPane(definition), 0, 1);
		add(new MainComponentBottomRightTabPane(definition), 1, 1);
	}

}
