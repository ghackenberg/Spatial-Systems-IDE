package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import de.tum.imomesa.workbench.diagrams.MonitorDiagram;
import de.tum.imomesa.workbench.diagrams.MonitorDiagramBehavior;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MonitorController implements AbstractEditorChildController<Monitor> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonSetInitialActivity;
	@FXML
	private Button buttonRemoveActivity;
	@FXML
	private Button buttonRemoveTransition;

	private MonitorDiagram diagram;

	@Override
	public void setRootElement(Monitor element) {
		diagram = new MonitorDiagram(pane, element, new MonitorDiagramBehavior(), true);

		buttonSetInitialActivity.setDisable(true);
		buttonRemoveActivity.setDisable(true);
		buttonRemoveTransition.setDisable(true);

		diagram.clickedElementProperty().addListener(new ChangeListener<Element>() {
			@Override
			public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				buttonSetInitialActivity.setDisable(!(newValue instanceof Activity));
				buttonRemoveActivity.setDisable(!(newValue instanceof Activity));
				buttonRemoveTransition.setDisable(!(newValue instanceof Transition));
			}
		});
	}
	
	@Override
	public void setSelectedElement(Element element) {
		Activity label = element.getFirstAncestorByType(Activity.class);
		Transition transition = element.getFirstAncestorByType(Transition.class);
		Monitor executable = element.getFirstAncestorByType(Monitor.class);
		
		if (label != null) {
			diagram.setClickedElement(label);
		} else if (transition != null) {
			diagram.setClickedElement(transition);
		} else if (executable != null) {
			diagram.setClickedElement(executable);
		}
	}

	@FXML
	public void setInitialActivity() {
		diagram.getElement().setInitialLabel((Activity) diagram.getClickedElement());
	}

	@FXML
	public void removeActivity() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

	@FXML
	public void removeTransition() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}
