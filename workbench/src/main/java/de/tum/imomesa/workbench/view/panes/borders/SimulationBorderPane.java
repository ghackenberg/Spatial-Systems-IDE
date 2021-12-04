package de.tum.imomesa.workbench.view.panes.borders;

import de.tum.imomesa.workbench.view.bars.tools.FooterToolBar;

public class SimulationBorderPane extends AbstractBorderPane {

	public SimulationBorderPane() {
		setBottom(new FooterToolBar());
	}

}
