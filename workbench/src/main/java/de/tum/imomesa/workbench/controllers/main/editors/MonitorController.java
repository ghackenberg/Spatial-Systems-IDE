package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.workbench.diagrams.MonitorDiagramBehavior;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import de.tum.imomesa.workbench.diagrams.MonitorDiagram;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MonitorController implements AbstractElementController<Monitor> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonSetInitialActivity;
	@FXML
	private Button buttonRemoveTransition;

	private MonitorDiagram diagram;

	@Override
	public void setElement(Monitor element) {
		diagram = new MonitorDiagram(pane, element, new MonitorDiagramBehavior(), true);

		buttonSetInitialActivity.setDisable(true);
		buttonRemoveTransition.setDisable(true);

		diagram.clickedElementProperty().addListener(new ChangeListener<Element>() {
			@Override
			public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				buttonSetInitialActivity.setDisable(!(newValue instanceof Activity));
				buttonRemoveTransition.setDisable(!(newValue instanceof Transition));
			}
		});
	}

	@FXML
	public void setInitialActivity() {
		diagram.getElement().setInitialLabel((Activity) diagram.getClickedElement());
	}

	@FXML
	public void removeTransition() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}
