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
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class ComponentsController implements AbstractEditorChildController<DefinitionComponent> {
	
	@FXML
	private Pane pane;
	@FXML
	private Button buttonRemoveComponent;
	@FXML
	private Button buttonRemovePort;
	@FXML
	private Button buttonRemoveChannel;
	
	private ComponentDiagram diagram;

	@Override
	public void setRootElement(DefinitionComponent element) {
		diagram = new ComponentDiagram(pane, element, new ComponentDiagramBehavior(), true);

		buttonRemoveComponent.setDisable(true);
		buttonRemovePort.setDisable(true);
		buttonRemoveChannel.setDisable(true);
		
		diagram.clickedElementProperty().addListener(new ChangeListener<Element>() {
			@Override
			public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				buttonRemoveComponent.setDisable(!(newValue instanceof Component<?>) || newValue == diagram.getElement());
				buttonRemovePort.setDisable(!(newValue instanceof Port));
				buttonRemoveChannel.setDisable(!(newValue instanceof Channel));
			}
		});
	}
	
	@Override
	public void setSelectedElement(Element element) {
		diagram.setClickedElement(element);
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