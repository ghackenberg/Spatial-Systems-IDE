package de.tum.imomesa.workbench.controllers.main.editors.component;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.model.components.DefinitionComponent;
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
	private Button buttonRemoveStaticChannel;
	
	private ComponentDiagram diagram;

	@Override
	public void setRootElement(DefinitionComponent element) {
		diagram = new ComponentDiagram(pane, element, new ComponentDiagramBehavior(), true);
		
		buttonRemoveStaticChannel.setDisable(true);
		
		diagram.clickedElementProperty().addListener(new ChangeListener<Element>() {
			@Override
			public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				buttonRemoveStaticChannel.setDisable(!(newValue instanceof StaticChannel));
			}
		});
	}
	
	@Override
	public void setSelectedElement(Element element) {
		diagram.setClickedElement(element);
	}
	
	@FXML
	public void removeStaticChannel() {
		new RemoveElementHandler(diagram.getClickedElement()).handle(null);
	}

}