package de.tum.imomesa.model.executables;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.properties.Property;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public abstract class Label extends NamedElement {

	public Label() {
		/*
		 * // create object Point p = new Point(this); setPoint(p);
		 */
	}

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Action a : actions) {
			a.accept(visitor);
		}
		for (Property p : properties) {
			p.accept(visitor);
		}
		/*
		 * if (getPoint() != null) { getPoint().accept(visitor); }
		 */
	}

	// Actions
	private ListProperty<Action> actions = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Action> getActions() {
		return actions.get();
	}

	public void setActions(List<Action> actions) {
		this.actions.set(FXCollections.observableList(actions));
	}

	@Cascading
	public ListProperty<Action> actionsProperty() {
		return actions;
	}

	// Properties
	private final ListProperty<Property> properties = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Property> getProperties() {
		return properties.get();
	}

	public void setProperties(List<Property> properties) {
		this.properties.set(FXCollections.observableList(properties));
	}

	@Cascading
	public ListProperty<Property> propertiesProperty() {
		return properties;
	}

	/*
	 * // Point private ObjectProperty<Point> point = new
	 * SimpleObjectProperty<>();
	 * 
	 * public Point getPoint() { return point.get(); }
	 * 
	 * public void setPoint(Point p) { this.point.set(p); }
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

}
