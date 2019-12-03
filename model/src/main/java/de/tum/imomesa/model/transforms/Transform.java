package de.tum.imomesa.model.transforms;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

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
	
	public static eu.mihosoft.vvecmath.Transform toVVecMathTransform(RealMatrix matrix) {
		double[] values = new double[16];
		
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				values[r * 4 + c] = matrix.getEntry(r, c);
			}
		}
		
		return eu.mihosoft.vvecmath.Transform.from(values);
	}
	
	public static eu.mihosoft.vvecmath.Transform toVVecMathTransform(List<Transform> transforms) {
		eu.mihosoft.vvecmath.Transform result = eu.mihosoft.vvecmath.Transform.unity();
		
		for (Transform transform : transforms) {
			result = transform.toVVecMathTransform().apply(result);
		}
				
		return result;
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
	
	protected void handleVectorChange(ObservableValue<? extends Point> property, Point oldValue, Point newValue) {
		oldValue.xProperty().removeListener(this::handleNumberChange);
		oldValue.yProperty().removeListener(this::handleNumberChange);
		oldValue.zProperty().removeListener(this::handleNumberChange);
		
		newValue.xProperty().addListener(this::handleNumberChange);
		newValue.yProperty().addListener(this::handleNumberChange);
		newValue.zProperty().addListener(this::handleNumberChange);
		
		setTransform(toVVecMathTransform());
	}
	
	protected void handleNumberChange(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {
		setTransform(toVVecMathTransform());
	}
	
}
