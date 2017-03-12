package de.tum.imomesa.utilities.visitors;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.expressions.ObservationExpression;
import de.tum.imomesa.model.ports.LifeMaterialPort;

public class ElementUsageVisitor extends SingleMethodVisitor {
	
	// attributes
	private Element element;
	private boolean result;
	
	// constructor
	public ElementUsageVisitor(Element e) {
		this.element = e;
		result = false;
	}
	
	// visit action
	public void visit(Action action) {
		if(action.getObservation() != null && action.getObservation().equals(element)) {
			result = true;
		}
	}
	
	// visit observation expression
	public void visit(ObservationExpression expression) {
		if(expression.getObservation() != null && expression.getObservation().equals(element)) {
			result = true;
		}
	}
	
	public void visit(LifeMaterialPort port) {
		if(port.getComponent() != null && port.getComponent().equals(element)) {
			result = true;
		}
	}
	
	public void visit(ReferenceComponent comp) {
		if(comp.getTemplate() != null && comp.getTemplate().equals(element)) {
			result = true;
		}
	}
	
	// return result
	public boolean elementOccursInModel() {
		return result;
	}
}
