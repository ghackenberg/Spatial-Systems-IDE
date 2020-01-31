package de.tum.imomesa.workbench.controllers.main.editors;

import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.monitors.Activity;
import de.tum.imomesa.model.executables.monitors.Monitor;
import de.tum.imomesa.model.executables.monitors.Transition;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import de.tum.imomesa.workbench.diagrams.MonitorDiagram;
import de.tum.imomesa.workbench.diagrams.MonitorDiagramBehavior;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MonitorController implements AbstractEditorChildController<Monitor> {

	@FXML
	private Pane pane;
	@FXML
	private Button buttonCreateActivity;
	@FXML
	private Button buttonSetInitialActivity;
	@FXML
	private Button buttonRemoveActivity;
	@FXML
	private Button buttonRemoveTransition;

	private MonitorDiagram diagram;

	@FXML
	public void initialize() {
		buttonSetInitialActivity.setDisable(true);
		buttonRemoveActivity.setDisable(true);
		buttonRemoveTransition.setDisable(true);
	}

	@Override
	public void setRootElement(Monitor element) {
		pane.getChildren().clear();

		final ChangeListener<Element> listener = (observable, oldValue, newValue) -> {
			buttonSetInitialActivity.setDisable(!checkType(Activity.class) || checkObject(element.getInitialLabel()));
			buttonRemoveActivity.setDisable(!checkType(Activity.class));
			buttonRemoveTransition.setDisable(!checkType(Transition.class));
		};

		diagram = new MonitorDiagram(pane, element, new MonitorDiagramBehavior(), true);
		diagram.clickedElementProperty().addListener(listener);
		diagram.pressedElementProperty().addListener(listener);

		// TODO Remove listener when not needed anymore!
		element.initialLabelProperty().addListener(listener);
	}

	private <T extends Element> boolean checkObject(T object) {
		return object != null && (diagram.getClickedElement() == object || diagram.getPressedElement() == object);
	}

	private <T extends Element> boolean checkType(Class<T> type) {
		return checkType(diagram.getClickedElement(), type) || checkType(diagram.getPressedElement(), type);
	}

	private <T extends Element> boolean checkType(Element element, Class<T> type) {
		return element != null && type.isAssignableFrom(element.getClass());
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
	public void createActivity() {
		Activity activity = new Activity();
		activity.setName("New activity");
		activity.setParent(diagram.getElement());
		activity.accept(new SyntacticCheckVisitor());

		diagram.getElement().getLabels().add(activity);
		diagram.setClickedElement(activity);
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
