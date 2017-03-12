package de.tum.imomesa.workbench.controllers.simulation.editors;

import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.workbench.commons.events.SimulationStepEvent;
import de.tum.imomesa.workbench.controllers.simulation.AbstractElementController;
import de.tum.imomesa.workbench.diagrams.ExecutableDiagram;

public abstract class ExecutableController<E extends Executable<?, ?>> extends AbstractElementController<E> {

	protected ExecutableDiagram<?, ?, ?> diagram;
	
	@Override
	public void handle(SimulationStepEvent event) {
		try {
			// Obtain simulator memory
			Memory memory = simulator.getMemory();
			// Obtain active step
			int step = event.getStep();
			// Obtain active transition
			Transition<?> transition = null;
			if (memory.hasTransition(extendedContext, step)) {
				transition = memory.getTransition(extendedContext, step);
			}
			// Set clicked element
			diagram.setClickedElement(transition);
			// Obtain active label
			Label label = null;
			if (memory.hasLabel(extendedContext, step)) {
				label = memory.getLabel(extendedContext, step);
			}
			// Set clicked element
			diagram.setPressedElement(label);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
