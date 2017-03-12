package de.tum.imomesa.model.transforms;

import org.apache.commons.math3.linear.RealMatrix;

public class RawTransform extends Transform {

	// TODO provide matrix coefficient for storage! 
	
	private RealMatrix matrix;

	public RawTransform() {
		
	}
	
	public RawTransform(RealMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public Transform multiplyTransform(double multiplicator) {
		return new RawTransform(matrix.scalarMultiply(multiplicator));
	}

	@Override
	public RealMatrix toRealMatrix() {
		return matrix;
	}

}
