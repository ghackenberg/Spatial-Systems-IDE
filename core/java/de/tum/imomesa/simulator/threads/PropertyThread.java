package de.tum.imomesa.simulator.threads;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.Component;
import de.tum.imomesa.model.elements.executables.Executable;
import de.tum.imomesa.model.elements.executables.Label;
import de.tum.imomesa.model.elements.properties.ConstraintProperty;
import de.tum.imomesa.model.elements.properties.Property;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.Utils;
import de.tum.imomesa.simulator.dispatcher.ExpressionDispatcher;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.managers.ThreadManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;

public class PropertyThread extends AbstractThread<Property> {

	public PropertyThread(List<Element> context, Property element, Memory memory, int step) {
		super(context, element, memory, step);
	}

	@Override
	protected void execute() {
		try {
			// Get last element
			Element last = context.get(context.size() - 1);
			// Check for label
			if (last instanceof Label) {
				// Wait for executable
				ThreadManager.getInstance().joinThread(this, Utils.getExecutableThread(context.subList(0, context.size() - 2), (Executable<?, ?>) context.get(context.size() - 2)));
			}
			// Check for active label
			if (!(last instanceof Label) || memory.getLabel(context.subList(0, context.size() - 1), step).equals(last)) {
				// Evaluate expression
				Object value = null;
				// Obtain value
				if (context.get(context.size() - 1) instanceof Component) {
					value = ExpressionDispatcher.getInstance().dispatch(this, context, element.getExpression(), memory, step);
				}
				else if (context.get(context.size() - 1) instanceof Label) {
					value = ExpressionDispatcher.getInstance().dispatch(this, context.subList(0, context.size() - 1), element.getExpression(), memory, step);
				}
				else {
					throw new IllegalStateException();
				}
				// Remember value
				ObservationDispatcher.getInstance().dispatch(this, context, element, memory, step, value);
				// Check constraint
				if(element instanceof ConstraintProperty) {
					// Convert value
					Boolean result = (Boolean) value;
					// Check constraint
					if(result == false) {
						// Add marker
						MarkerManager.get().addMarker(new ErrorMarker(element.append(context), "Constraint is violated!", step));
					}
				}
			}
			// Not active label!
			else {
				// Set null
				ObservationDispatcher.getInstance().dispatch(this, context, element, memory, step, null);
			}
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
