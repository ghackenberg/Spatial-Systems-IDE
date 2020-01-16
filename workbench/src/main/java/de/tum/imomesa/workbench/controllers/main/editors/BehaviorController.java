package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.behaviors.Behavior;
import de.tum.imomesa.model.executables.behaviors.State;
import de.tum.imomesa.model.executables.behaviors.Transition;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import de.tum.imomesa.workbench.diagrams.BehaviorDiagram;
import de.tum.imomesa.workbench.diagrams.BehaviorDiagramBehavior;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class BehaviorController implements AbstractEditorChildController<Behavior> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonSetInitialState;
	@FXML
	private Button buttonRemoveState;
	@FXML
	private Button buttonRemoveTransition;
	
	private BehaviorDiagram diagram;
	
	@FXML
	public void initialize() {
		buttonSetInitialState.setDisable(true);
		buttonRemoveState.setDisable(true);
		buttonRemoveTransition.setDisable(true);
	}

	@Override
	public void setRootElement(Behavior element) {
		pane.getChildren().clear();
		
		final ChangeListener<Element> listener = (observable, oldValue, newValue) -> {
			buttonSetInitialState.setDisable(!checkType(State.class));
			buttonRemoveState.setDisable(!checkType(State.class));
			buttonRemoveTransition.setDisable(!checkType(Transition.class));
		};
		
		diagram = new BehaviorDiagram(pane, element, new BehaviorDiagramBehavior(), true);
		diagram.clickedElementProperty().addListener(listener);
		diagram.pressedElementProperty().addListener(listener);
	}
	
	private <T extends Element> boolean checkType(Class<T> type) {
		return checkType(diagram.getClickedElement(), type) || checkType(diagram.getPressedElement(), type);
	}
	
	private <T extends Element> boolean checkType(Element element, Class<T> type) {
		return element != null && type.isAssignableFrom(element.getClass()); 
	}
	
	@Override
	public void setSelectedElement(Element element) {
		State label = element.getFirstAncestorByType(State.class);
		Transition transition = element.getFirstAncestorByType(Transition.class);
		Behavior executable = element.getFirstAncestorByType(Behavior.class);
		
		if (label != null) {
			diagram.setClickedElement(label);
		} else if (transition != null) {
			diagram.setClickedElement(transition);
		} else if (executable != null) {
			diagram.setClickedElement(executable);
		}
	}
	
	@FXML
	public void setInitialState() {
		diagram.getElement().setInitialLabel((State) diagram.getClickedElement());
	}
	
	@FXML
	public void removeState() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}
	
	@FXML
	public void removeTransition() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}
