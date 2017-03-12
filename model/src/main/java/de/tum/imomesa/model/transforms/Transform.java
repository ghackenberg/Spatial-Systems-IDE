package de.tum.imomesa.model.transforms;

import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.model.Element;

public abstract class Transform extends Element {

	public abstract Transform multiplyTransform(double multiplicator);

	public abstract RealMatrix toRealMatrix();

}
