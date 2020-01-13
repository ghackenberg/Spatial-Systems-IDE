package de.tum.imomesa.workbench.controllers;

import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.renderer.nodes.JMECanvas;
import de.tum.imomesa.workbench.commons.events.SelectionDiagramEvent;
import de.tum.imomesa.workbench.commons.events.SelectionExplorerEvent;
import de.tum.imomesa.workbench.controllers.main.AbstractElementController;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class JMEController implements AbstractElementController<Element>, EventHandler {

	@FXML
	private BorderPane borderPane;
	@FXML
	private Pane pane;
	
	@FXML
	public void initialize() {
		pane.getChildren().add(JMECanvas.getInstance());
		
		JMECanvas.getInstance().widthProperty().bind(pane.widthProperty());
		JMECanvas.getInstance().heightProperty().bind(pane.heightProperty());
	}
	@Override
	public void setElement(Element element) {
		JMECanvas.getInstance().getApplication().setComponent(element.getFirstAncestorByType(Component.class));
	}
	
	public void handle(SelectionExplorerEvent event) {
		if (event.getSelected() instanceof OverviewElement) {
			JMECanvas.getInstance().getApplication().setComponent(event.getSelected().getParent().getFirstAncestorByType(Component.class));
		} else {
			JMECanvas.getInstance().getApplication().setComponent(event.getSelected().getFirstAncestorByType(Component.class));
		}
	}

	public void handle(SelectionDiagramEvent event) {
		if (event.getSelected() instanceof OverviewElement) {
			JMECanvas.getInstance().getApplication().setComponent(event.getSelected().getParent().getFirstAncestorByType(Component.class));
		} else {
			JMECanvas.getInstance().getApplication().setComponent(event.getSelected().getFirstAncestorByType(Component.class));
		}
	}
	
}
