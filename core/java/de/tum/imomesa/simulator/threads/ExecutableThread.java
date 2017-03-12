package de.tum.imomesa.simulator.threads;

import java.util.LinkedList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.executables.Action;
import de.tum.imomesa.model.elements.executables.Executable;
import de.tum.imomesa.model.elements.executables.Label;
import de.tum.imomesa.model.elements.executables.Transition;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.dispatcher.ExpressionDispatcher;
import de.tum.imomesa.simulator.dispatcher.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import de.tum.imomesa.simulator.markers.WarningMarker;

public abstract class ExecutableThread<L extends Label, T extends Transition> extends AbstractThread<Executable<L, T>> {
	
	public ExecutableThread(List<Element> context, Executable<L, T> executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() {
		try {
			// Select enabled transitions
			
			List<T> enabled = new LinkedList<>();
			
			for (T transition : getOutgoingTransitions((L) memory.getLabel(element.append(context), step - 1))) {
				// Evaluate guard
				Object result = ExpressionDispatcher.getInstance().dispatch(this, element.append(context), transition.getGuard().getExpression(), memory, step);
				// Check result
				if (result != null && (Boolean) result) {
					// Remember transition
					enabled.add(transition);
				}
			}
			
			// Select executable actions
			
			List<Action> actions;
			
			if (enabled.size() == 0) {
				// Obtain actions
				actions = memory.getLabel(element.append(context), step - 1).getActions();
				// Update active state
				memory.setLabel(element.append(context), step, memory.getLabel(element.append(context), step - 1));
			}
			else {
				// warning if more than one transition available
				if(enabled.size() > 1) {
					// Create message
					String errorText = "Not deterministic behavior in Behavior \"" + getName() + "\" with " + enabled.size() + " transitions.";
					// Create marker
					MarkerManager.get().addMarker(new WarningMarker(context, errorText, step));
				}
				// Obtain transition
				T selected = enabled.get((int) Math.floor(Math.random() * enabled.size()));
				// Obtain actions
				actions = selected.getActions();
				// Update active state
				memory.setLabel(element.append(context), step, getTargetLabel(selected));
			}
			
			// Calculate actions
			
			for (Action action : actions) {
				// Evaluate expression
				Object value = ExpressionDispatcher.getInstance().dispatch(this, element.append(context), action.getExpression(), memory, step);
				// Remeber value
				ObservationDispatcher.getInstance().dispatch(this, action.getObservation().resolve(context), action.getObservation(), memory, step, value);
			}
		} catch (InterruptedException e) {
			MarkerManager.get().addMarker(new ErrorMarker(element.append(context), e.getMessage(), step));
		}
	}
	
	protected abstract List<T> getOutgoingTransitions(L label);
	protected abstract L getTargetLabel(T transition);

}
