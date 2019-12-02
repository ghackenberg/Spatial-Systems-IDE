package de.tum.imomesa.model.transforms;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TranslationTransform extends Transform {

	public TranslationTransform() {
		vector.addListener(this::handleVectorChange);
		
		getVector().xProperty().addListener(this::handleNumberChange);
		getVector().yProperty().addListener(this::handleNumberChange);
		getVector().zProperty().addListener(this::handleNumberChange);
		
		setTransform(toVVecMathTransform());
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getVector() != null) {
			getVector().accept(visitor);
		}
	}

	@Override
	public Transform multiplyTransform(double multiplicator) {

		TranslationTransform result = new TranslationTransform();

		Point v = getVector();
		result.getVector().setX(v.getX() * multiplicator);
		result.getVector().setY(v.getY() * multiplicator);
		result.getVector().setZ(v.getZ() * multiplicator);

		return result;
	}

	@Override
	public RealMatrix toRealMatrix() {
		return new BlockRealMatrix(new double[][] { { 1, 0, 0, getVector().getX() }, { 0, 1, 0, getVector().getY() },
				{ 0, 0, 1, getVector().getZ() }, { 0, 0, 0, 1 } });
	}

	// Move vector
	private ObjectProperty<Point> vector = new SimpleObjectProperty<>(new Point(this));

	public Point getVector() {
		return vector.get();
	}

	public void setVector(Point vector) {
		this.vector.set(vector);
	}

	@Cascading
	public ObjectProperty<Point> vectorProperty() {
		return vector;
	}
	
}
