package de.tum.imomesa.workbench.diagrams;

import de.tum.imomesa.model.Element;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

public abstract class AbstractDiagramBehavior<E extends Element> {
	
	private AbstractDiagram<E> graph;
	
	public void setDiagram(AbstractDiagram<E> graph) {
		this.graph = graph;
	}
	
	public AbstractDiagram<E> getDiagram() {
		return graph;
	}

	public abstract void handleMousePressed(MouseEvent event, Element element, Node node);
	public abstract void handleMouseDragged(MouseEvent event, Element element, Node node);
	public abstract void handleMouseReleased(MouseEvent event, Element element, Node node);

	protected void alert(String headerText, String contextText) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle("Error message");
		alert.setHeaderText(headerText);
		alert.setContentText(contextText);

		alert.showAndWait();
	}
	
}
