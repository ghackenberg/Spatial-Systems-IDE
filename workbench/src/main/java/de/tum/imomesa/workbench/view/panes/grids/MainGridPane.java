package de.tum.imomesa.workbench.view.panes.grids;

import de.tum.imomesa.workbench.view.panes.borders.MainObjectBorderPane;
import de.tum.imomesa.workbench.view.panes.tabs.MainExplorerTabPane;

public class MainGridPane extends AbstractGridPane {

	public MainGridPane() {
		super(new double[] { 20, 80 }, new double[] { 100 });
		add(new MainExplorerTabPane(), 0, 0);
		add(new MainObjectBorderPane(), 1, 0);
	}

}
