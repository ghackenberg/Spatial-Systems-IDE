package de.tum.imomesa.simulator.evaluators;

import java.util.List;

import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.elements.ComponentInterface;
import de.tum.imomesa.model.elements.transforms.Transform;
import de.tum.imomesa.simulator.Memory;

public abstract class ComponentInterfaceEvaluator<T extends ComponentInterface> extends AbstractEvaluator<T> {

	public ComponentInterfaceEvaluator(List<Element> context, T element, Memory memory, int step) {
		super(context, element, memory, step);
	}
	
	@Override
	public void prepare() throws InterruptedException {
		memory.initTransform(element.append(context), step);
		memory.initCollision(element.append(context), step);
	}
	
	@Override
	public void initialize() throws InterruptedException {
		RealMatrix parentMatrix = new DiagonalMatrix(new double[]{1.,1.,1.,1.});
		// get transform matrix from parent
		if(context.size() > 0) {
			parentMatrix = memory.getTransform(context, step);
		}
		// add own transforms
		for(Transform t : element.getTransforms()) {
			parentMatrix = parentMatrix.multiply(t.toRealMatrix());
		}
		// store transform in memory
		memory.setTransform(element.append(context), step, parentMatrix);
		
	}
}
