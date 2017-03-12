package de.tum.imomesa.workbench.explorers.handlers;

import java.util.Optional;

import de.tum.imomesa.checker.visitor.SyntacticCheckVisitor;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;

public abstract class AbstractAddElementHandler<T extends Element, S extends Property<?>> implements EventHandler<ActionEvent> {
	
	private Element parent;
	private Class<T> type;
	private Dialog<T> dialog;
	
	protected S property;
	
	public AbstractAddElementHandler(Element parent, S property, Class<T> type) {
		this.parent = parent;
		this.property = property;
		this.type = type;
	}
	
	public AbstractAddElementHandler(Element parent, S property, Dialog<T> dialog) {
		this.parent = parent;
		this.property = property;
		this.dialog = dialog;
	}
	
	@Override
	public void handle(ActionEvent event) {
		T element = createElement();
		if(element != null) {
			element.setParent(parent);
			updateProperty(element);
			element.accept(new SyntacticCheckVisitor());
		}
	}
	
	// Step 1: Creation
	private T createElement() {
		T element = null;
		
		if(dialog != null) {
			Optional<T> result = dialog.showAndWait();
			if(result.isPresent()) {
				element = result.get();
			}
			else {
				return null;
			}
		}
		else {
			try {
				element = type.newInstance();
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		if(element instanceof NamedElement) {
			String[] words = element.getClass().getSimpleName().split("(?=\\p{Upper})");
			String name = "";
			for (String word : words) {
				name += " " + word.toLowerCase();
			}
			
			((NamedElement)element).setName("New" + name);
		}
		
		return element;
	}
	
	protected abstract void updateProperty(T element);
	
}
