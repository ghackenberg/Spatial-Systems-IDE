package de.tum.imomesa.workbench.controllers.main.editors.component;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.Channel;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.workbench.controllers.AbstractEditorChildController;
import de.tum.imomesa.workbench.diagrams.ComponentDiagram;
import de.tum.imomesa.workbench.diagrams.ComponentDiagramBehavior;
import de.tum.imomesa.workbench.explorers.handlers.RemoveElementHandler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class ComponentsController implements AbstractEditorChildController<DefinitionComponent> {
	
	@FXML
	private Pane pane;
	@FXML
	private Button buttonCreateComponent;
	@FXML
	private Button buttonCreatePort;
	@FXML
	private Button buttonRemoveComponent;
	@FXML
	private Button buttonRemovePort;
	@FXML
	private Button buttonRemoveChannel;
	
	private ComponentDiagram diagram;
	
	@FXML
	public void initialize() {
		buttonRemoveComponent.setDisable(true);
		buttonRemovePort.setDisable(true);
		buttonRemoveChannel.setDisable(true);
	}

	@Override
	public void setRootElement(DefinitionComponent element) {
		pane.getChildren().clear();
		
		final ChangeListener<Element> listener = (observable, oldValue, newValue) -> {
			buttonRemoveComponent.setDisable(!checkType(Component.class) || checkObject(element));
			buttonRemovePort.setDisable(!checkType(Port.class));
			buttonRemoveChannel.setDisable(!checkType(Channel.class));
		};
		
		diagram = new ComponentDiagram(pane, element, new ComponentDiagramBehavior(), true);
		diagram.clickedElementProperty().addListener(listener);
		diagram.pressedElementProperty().addListener(listener);
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
		diagram.setClickedElement(element);
	}
	
	@FXML
	public void createComponent() {
		// TODO implement create component action
	}
	
	@FXML
	public void createPort() {
		// TODO implement create port action
	}
	
	@FXML
	public void removeComponent() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}
	
	@FXML
	public void removePort() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}
	
	@FXML
	public void removeChannel() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}