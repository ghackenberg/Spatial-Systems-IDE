package de.tum.imomesa.simulator.threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Action;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.simulator.dispatchers.ExpressionDispatcher;
import de.tum.imomesa.simulator.dispatchers.ObservationDispatcher;
import de.tum.imomesa.simulator.managers.MarkerManager;
import de.tum.imomesa.simulator.markers.errors.InterruptedExceptionError;
import de.tum.imomesa.simulator.markers.warnings.NonDeterministicChoiceWarning;

public abstract class ExecutableThread<L extends Label, T extends Transition<L>> extends AbstractThread<Executable<L, T>> {
	
	public ExecutableThread(List<Element> context, Executable<L, T> executable, Memory memory, int step) {
		super(context, executable, memory, step);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() {
		try {
			// Get active label
			
			L label = (L) memory.getLabel(element.append(context), step - 1);
			
			// Select enabled transitions
			
			List<T> enabled = new LinkedList<>();
			
			for (T transition : getOutgoingTransitions(label)) {
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
				actions = label != null ? label.getActions() : new ArrayList<>();
				// Update active state
				memory.setLabel(element.append(context), step, label);
				// Update active transition
				memory.setTransition(element.append(context), step, null);
			}
			else {
				// warning if more than one transition available
				if(enabled.size() > 1) {
					// Create marker
					MarkerManager.get().addMarker(new NonDeterministicChoiceWarning<L, T, Executable<L, T>>(context, element, label, enabled, enabled.size(), step));
				}
				// Obtain transition
				T selected = enabled.get((int) Math.floor(Math.random() * enabled.size()));
				// Obtain actions
				actions = selected.getActions();
				// Update active state
				memory.setLabel(element.append(context), step, getTargetLabel(selected));
				// Update active transition
				memory.setTransition(element.append(context), step, selected);
			}
			
			// Calculate actions
			
			for (Action action : actions) {
				if (action.getObservation() != null && action.getExpression() != null) {
					// Evaluate expression
					Object value = ExpressionDispatcher.getInstance().dispatch(this, element.append(context), action.getExpression(), memory, step);
					// Remeber value
					ObservationDispatcher.getInstance().dispatch(this, action.getObservation().resolve(context), action.getObservation(), memory, step, value);
				}
			}
		} catch (InterruptedException e) {
			MarkerManager.get().addMarker(new InterruptedExceptionError(element.append(context), e, step));
		}
	}
	
	protected abstract List<T> getOutgoingTransitions(L label);
	protected abstract L getTargetLabel(T transition);

}
