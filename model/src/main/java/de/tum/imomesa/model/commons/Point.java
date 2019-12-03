package de.tum.imomesa.model.commons;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import de.tum.imomesa.model.Element;

public class Point extends Element {
	
	public Point() {
		
	}
	
	public Point(Element parent) {
		setParent(parent);
	}
	
	public Point(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public Point(Element parent, double x, double y, double z) {
		setParent(parent);
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public Double dotProduct(Point p) {
		return getX() * p.getX() + getY() * p.getY() + getZ() * p.getZ();
	}
	
	public Double getAngleRadians(Point p) {
		return Math.acos(dotProduct(p) / (getLenght() * p.getLenght()));
	}
	
	public Double getAngleDegree(Point p) {
		return getAngleRadians(p) * 360 / ( 2 * Math.PI);
	}
	
	public Point crossProduct(Point p) {
		Point result = new Point();
		
		result.setX(getY() * p.getZ() - p.getY() * getZ());
		result.setY(getZ() * p.getX() - p.getZ() * getX());
		result.setZ(getX() * p.getY() - p.getX() * getY());
		
		return result;
	}
	
	public Double getLenght() {
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
	}
	
	public String toString() {
		return "( " + x.getValue() + " | " + y.getValue() + " | " + z.getValue() + " )";
	}
	
	// X
	private DoubleProperty x = new SimpleDoubleProperty(0);
	
	public double getX()
	{
		return x.get();
	}
	public void setX(double x)
	{
		this.x.set(x);
	}
	public DoubleProperty xProperty()
	{
		return x;
	}
	
	// Y
	private DoubleProperty y = new SimpleDoubleProperty(0);
	
	public double getY()
	{
		return y.get();
	}
	public void setY(double y)
	{
		this.y.set(y);
	}
	public DoubleProperty yProperty()
	{
		return y;
	}
	
	// Z
	private DoubleProperty z = new SimpleDoubleProperty(0);
	
	public double getZ()
	{
		return z.get();
	}
	public void setZ(double z)
	{
		this.z.set(z);
	}
	public DoubleProperty zProperty()
	{
		return z;
	}

}
