package de.tum.imomesa.model.components;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public abstract class Component<T extends Port> extends NamedElement {

	public Component() {
		/*
		 * // create object setPoint(new Point(this));
		 */

		// init
		setCameraDistance(-400.);
		setCameraAngleX(-45.);
		setCameraAngleY(-45.);

		setCameraTranslateX(0.);
		setCameraTranslateY(0.);
		setCameraTranslateZ(0.);
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Port p : ports) {
			p.accept(visitor);
		}

		for (Transform t : transforms) {
			t.accept(visitor);
		}
		/*
		 * if(getPoint() != null) { getPoint().accept(visitor); }
		 */
	}

	// Ports
	private final ListProperty<T> ports = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<T> getPorts() {
		return ports.getValue();
	}

	public void setPorts(List<T> ports) {
		this.ports.set(FXCollections.observableList(ports));
	}

	@Cascading
	public ListProperty<T> portsProperty() {
		return ports;
	}

	// Transforms
	private final ListProperty<Transform> transforms = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Transform> getTransforms() {
		return transforms.get();
	}

	public void setTransforms(List<Transform> transforms) {
		this.transforms.set(FXCollections.observableList(transforms));
	}

	@Cascading
	public ListProperty<Transform> transformsProperty() {
		return transforms;
	}

	// Point
	/*
	 * private ObjectProperty<Point> point = new SimpleObjectProperty<>();
	 * 
	 * public Point getPoint() { return point.get(); } public void
	 * setPoint(Point p) { this.point.set(p); }
	 * 
	 * @Cascading public ObjectProperty<Point> pointProperty() { return point; }
	 */

	// X
	private DoubleProperty x = new SimpleDoubleProperty(0.2 + Math.random() * 0.6);

	public Double getX() {
		return x.get();
	}

	public void setX(Double x) {
		this.x.set(x);
	}

	public DoubleProperty xProperty() {
		return x;
	}

	// Y
	private DoubleProperty y = new SimpleDoubleProperty(0.1 + Math.random() * 0.8);

	public Double getY() {
		return y.get();
	}

	public void setY(Double y) {
		this.y.set(y);
	}

	public DoubleProperty yProperty() {
		return y;
	}

	// Width
	private DoubleProperty width = new SimpleDoubleProperty(0.2);

	public Double getWidth() {
		return width.get();
	}

	public void setWidth(Double width) {
		this.width.set(width);
	}

	public DoubleProperty widthProperty() {
		return width;
	}

	// Height
	private DoubleProperty height = new SimpleDoubleProperty(0.1);

	public Double getHeight() {
		return height.get();
	}

	public void setHeight(Double height) {
		this.height.set(height);
	}

	public DoubleProperty heightProperty() {
		return height;
	}

	// Camera distance
	private DoubleProperty cameraDistance = new SimpleDoubleProperty();

	public Double getCameraDistance() {
		return cameraDistance.get();
	}

	public void setCameraDistance(Double cameraDistance) {
		this.cameraDistance.set(cameraDistance);
	}

	public DoubleProperty cameraDistanceProperty() {
		return cameraDistance;
	}

	// Camera angle X
	private DoubleProperty cameraAngleX = new SimpleDoubleProperty();

	public Double getCameraAngleX() {
		return cameraAngleX.get();
	}

	public void setCameraAngleX(Double cameraAngleX) {
		this.cameraAngleX.set(cameraAngleX);
	}

	public DoubleProperty cameraAngleXProperty() {
		return cameraAngleX;
	}

	// Camera angle Y
	private DoubleProperty cameraAngleY = new SimpleDoubleProperty();

	public Double getCameraAngleY() {
		return cameraAngleY.get();
	}

	public void setCameraAngleY(Double cameraAngleY) {
		this.cameraAngleY.set(cameraAngleY);
	}

	public DoubleProperty cameraAngleYProperty() {
		return cameraAngleY;
	}

	// Camera translate X
	private DoubleProperty cameraTranslateX = new SimpleDoubleProperty();

	public Double getCameraTranslateX() {
		return cameraTranslateX.get();
	}

	public void setCameraTranslateX(Double cameraTranslateX) {
		this.cameraTranslateX.set(cameraTranslateX);
	}

	public DoubleProperty cameraTranslateXProperty() {
		return cameraTranslateX;
	}

	// Camera translate Y
	private DoubleProperty cameraTranslateY = new SimpleDoubleProperty();

	public Double getCameraTranslateY() {
		return cameraTranslateY.get();
	}

	public void setCameraTranslateY(Double cameraTranslateY) {
		this.cameraTranslateY.set(cameraTranslateY);
	}

	public DoubleProperty cameraTranslateYProperty() {
		return cameraTranslateY;
	}

	// Camera translate Z
	private DoubleProperty cameraTranslateZ = new SimpleDoubleProperty();

	public Double getCameraTranslateZ() {
		return cameraTranslateZ.get();
	}

	public void setCameraTranslateZ(Double cameraTranslateZ) {
		this.cameraTranslateZ.set(cameraTranslateZ);
	}

	public DoubleProperty cameraTranslateZProperty() {
		return cameraTranslateZ;
	}

}