package de.tum.imomesa.model.transforms;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import com.jme3.math.Matrix4f;

import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.commons.Point;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public abstract class Transform extends Element {

	public abstract Transform multiplyTransform(double multiplicator);

	public abstract RealMatrix toRealMatrix();
	
	public eu.mihosoft.vvecmath.Transform toVVecMathTransform() {
		return toVVecMathTransform(toRealMatrix());
	}
	
	public com.jme3.math.Transform toJMETransform() {
		return toJMETransform(toMatrix());
	}
	
	public Matrix4f toMatrix() {
		return toMatrix(toRealMatrix());
	}
	
	public static eu.mihosoft.vvecmath.Transform toVVecMathTransform(RealMatrix matrix) {
		double[] values = new double[16];
		
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				values[r * 4 + c] = matrix.getEntry(r, c);
			}
		}
		
		return eu.mihosoft.vvecmath.Transform.from(values);
	}
	
	public static com.jme3.math.Transform toJMETransform(Matrix4f matrix) {
		com.jme3.math.Transform result = new com.jme3.math.Transform();
		
		result.fromTransformMatrix(matrix);
		
		return result;
	}
	
	public static Matrix4f toMatrix(RealMatrix matrix) {
		Matrix4f result = new Matrix4f();
		
		result.m00 = (float) matrix.getEntry(0, 0);
		result.m01 = (float) matrix.getEntry(0, 1);
		result.m02 = (float) matrix.getEntry(0, 2);
		result.m03 = (float) matrix.getEntry(0, 3);

		result.m10 = (float) matrix.getEntry(1, 0);
		result.m11 = (float) matrix.getEntry(1, 1);
		result.m12 = (float) matrix.getEntry(1, 2);
		result.m13 = (float) matrix.getEntry(1, 3);

		result.m20 = (float) matrix.getEntry(2, 0);
		result.m21 = (float) matrix.getEntry(2, 1);
		result.m22 = (float) matrix.getEntry(2, 2);
		result.m23 = (float) matrix.getEntry(2, 3);
		
		result.m30 = (float) matrix.getEntry(3, 0);
		result.m31 = (float) matrix.getEntry(3, 1);
		result.m32 = (float) matrix.getEntry(3, 2);
		result.m33 = (float) matrix.getEntry(3, 3);
		
		return result;
	}
	
	public static eu.mihosoft.vvecmath.Transform toVVecMathTransform(List<Transform> transforms) {
		eu.mihosoft.vvecmath.Transform result = eu.mihosoft.vvecmath.Transform.unity();
		
		for (Transform transform : transforms) {
			result = transform.toVVecMathTransform().apply(result);
		}
				
		return result;
	}
	
	public static com.jme3.math.Transform toJMETransform(List<Transform> transforms) {
		return toJMETransform(toMatrix(transforms));
	}
	 
	public static Matrix4f toMatrix(List<Transform> transforms) {
		Matrix4f matrix = Matrix4f.IDENTITY;
		
		for (de.tum.imomesa.model.transforms.Transform transform : transforms) {
			matrix = matrix.mult(transform.toMatrix());
		}
		
		return matrix;
	}
	
	// Transform
	private ObjectProperty<eu.mihosoft.vvecmath.Transform> transform = new SimpleObjectProperty<>(eu.mihosoft.vvecmath.Transform.unity());
	
	protected void setTransform(eu.mihosoft.vvecmath.Transform transform) {
		this.transform.set(transform);
	}
	
	public eu.mihosoft.vvecmath.Transform getTransform() {
		return transform.get();
	}
	
	@Transient
	public ObjectProperty<eu.mihosoft.vvecmath.Transform> transformProperty() {
		return transform;
	}
	
	// Matrix
	private ObjectProperty<Matrix4f> matrix = new SimpleObjectProperty<>(Matrix4f.IDENTITY);
	
	protected void setMatrix(Matrix4f matrix) {
		this.matrix.set(matrix);
	}
	
	public Matrix4f getMatrix() {
		return matrix.get();
	}
	
	@Transient
	public ObjectProperty<Matrix4f> matrixProperty() {
		return matrix;
	}
	
	protected void handleVectorChange(ObservableValue<? extends Point> property, Point oldValue, Point newValue) {
		oldValue.xProperty().removeListener(this::handleNumberChange);
		oldValue.yProperty().removeListener(this::handleNumberChange);
		oldValue.zProperty().removeListener(this::handleNumberChange);
		
		newValue.xProperty().addListener(this::handleNumberChange);
		newValue.yProperty().addListener(this::handleNumberChange);
		newValue.zProperty().addListener(this::handleNumberChange);
		
		setMatrix(toMatrix());
		setTransform(toVVecMathTransform());
	}
	
	protected void handleNumberChange(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {
		setMatrix(toMatrix());
		setTransform(toVVecMathTransform());
	}
	
}
