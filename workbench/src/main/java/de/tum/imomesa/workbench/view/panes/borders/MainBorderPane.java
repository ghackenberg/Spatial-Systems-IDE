package de.tum.imomesa.workbench.view.panes.borders;

import de.tum.imomesa.workbench.view.bars.tools.FooterToolBar;
import de.tum.imomesa.workbench.view.panes.grids.MainGridPane;

public class MainBorderPane extends AbstractBorderPane {

	public MainBorderPane() {
		setCenter(new MainGridPane());
		setBottom(new FooterToolBar());
	}

}
