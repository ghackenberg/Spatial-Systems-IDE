package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.behaviors.Transition;
import de.tum.imomesa.workbench.diagrams.BehaviorDiagramBehavior;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import de.tum.imomesa.workbench.diagrams.BehaviorDiagram;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class BehaviorController implements AbstractElementController<Behavior> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonSetInitialState;
	@FXML
	private Button buttonRemoveTransition;
	
	private BehaviorDiagram diagram;

	@Override
	public void setElement(Behavior element) {
		diagram = new BehaviorDiagram(pane, element, new BehaviorDiagramBehavior(), true);
		
		buttonSetInitialState.setDisable(true);
		buttonRemoveTransition.setDisable(true);
		
		diagram.clickedElementProperty().addListener(new ChangeListener<Element>() {
			@Override
			public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				buttonSetInitialState.setDisable(!(newValue instanceof State));
				buttonRemoveTransition.setDisable(!(newValue instanceof Transition));
			}
		});
	}
	
	@FXML
	public void setInitialState() {
		diagram.getElement().setInitialLabel((State) diagram.getClickedElement());
	}
	
	@FXML
	public void removeTransition() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}
