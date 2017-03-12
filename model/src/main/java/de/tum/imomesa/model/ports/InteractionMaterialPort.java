package de.tum.imomesa.model.ports;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class InteractionMaterialPort extends MaterialPort {

	public InteractionMaterialPort() {
		/*
		 * Point p = new Point(this); setPoint(p);
		 */
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Port p : ports) {
			p.accept(visitor);
		}
		/*
		 * if(getPoint() != null) { getPoint().accept(visitor); }
		 */
	}

	// Ports
	private ListProperty<DefinitionPort> ports = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<DefinitionPort> getPorts() {
		return ports.get();
	}

	public void setPorts(List<DefinitionPort> ports) {
		this.ports.set(FXCollections.observableList(ports));
	}

	@Cascading
	public ListProperty<DefinitionPort> portsProperty() {
		return ports;
	}

	// Binds volumes
	private BooleanProperty bindsVolumes = new SimpleBooleanProperty(false);

	public boolean getBindsVolumes() {
		return bindsVolumes.get();
	}

	public void setBindsVolumes(boolean value) {
		bindsVolumes.set(value);
	}

	public BooleanProperty bindsVolumesProperty() {
		return bindsVolumes;
	}

	// Always Active
	private BooleanProperty alwaysActive = new SimpleBooleanProperty(false);

	public boolean getAlwaysActive() {
		return alwaysActive.get();
	}

	public void setAlwaysActive(boolean value) {
		alwaysActive.set(value);
	}

	public BooleanProperty alwaysActiveProperty() {
		return alwaysActive;
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

	// Execution Priority
	private IntegerProperty executionPriority = new SimpleIntegerProperty(0);

	public int getExecutionPriority() {
		return executionPriority.get();
	}

	public void setExecutionPriority(int value) {
		executionPriority.set(value);
	}

	public IntegerProperty executionPriorityProperty() {
		return executionPriority;
	}

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
}
