package de.tum.imomesa.model.transforms;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.commons.Point;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RotationTransform extends Transform {

	public RotationTransform() {
		// create elements
		Point p = new Point(this);
		this.setRotationAxe(p);
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getRotationAxe() != null) {
			getRotationAxe().accept(visitor);
		}
	}

	@Override
	public Transform multiplyTransform(double multiplicator) {

		RotationTransform newTransform = new RotationTransform();

		// set values
		// multiply only angle
		newTransform.setRotationAxe(getRotationAxe());
		newTransform.setAngle(getAngle() * multiplicator);

		return newTransform;
	}

	@Override
	public RealMatrix toRealMatrix() {

		Rotation rot = new Rotation(
				new Vector3D(getRotationAxe().getX(), getRotationAxe().getY(), getRotationAxe().getZ()),
				getAngle() * 2 * Math.PI / 360);
		double[][] result = rot.getMatrix();

		return new BlockRealMatrix(new double[][] { { result[0][0], result[0][1], result[0][2], 0 },
				{ result[1][0], result[1][1], result[1][2], 0 }, { result[2][0], result[2][1], result[2][2], 0 },
				{ 0, 0, 0, 1 } });
	}

	// rotation axe
	private ObjectProperty<Point> rotationAxe = new SimpleObjectProperty<>();

	public Point getRotationAxe() {
		return rotationAxe.get();
	}

	public void setRotationAxe(Point rotationAxe) {
		this.rotationAxe.set(rotationAxe);
	}

	@Cascading
	public ObjectProperty<Point> rotationAxeProperty() {
		return rotationAxe;
	}

	// angle
	private DoubleProperty angle = new SimpleDoubleProperty();

	public Double getAngle() {
		return angle.get();
	}

	public void setAngle(Double angle) {
		this.angle.set(angle);
	}

	public DoubleProperty angleProperty() {
		return angle;
	}
}
