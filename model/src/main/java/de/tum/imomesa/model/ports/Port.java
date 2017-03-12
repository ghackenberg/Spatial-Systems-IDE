package de.tum.imomesa.model.ports;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.channels.DynamicChannel;
import de.tum.imomesa.model.channels.StaticChannel;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public abstract class Port extends Observation {

	// Enumeration

	public enum Direction {
		INPUT, OUTPUT, INPUT_OUTPUT
	}

	// Constructors

	public Port() {
		super();
	}

	// Methods

	public boolean checkDirection(Direction direction) {
		// checks if this port has the given direction
		if (getDirection().equals(direction.ordinal())) {
			return true;
		} else if (getDirection().equals(Direction.INPUT_OUTPUT.ordinal())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (getName() == null) {
			return this.getClass().getSimpleName();
		} else {
			return getName();
		}
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		// check incoming
		// only check for outgoing -> still all ports are checked as a port has
		// to be outgoing from somewhere
		for (StaticChannel c : outgoingStaticChannels) {
			c.accept(visitor);
		}
	}

	// Incoming

	private ListProperty<StaticChannel> incomingStaticChannels = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<StaticChannel> getIncomingStaticChannels() {
		return incomingStaticChannelsProperty().get();
	}

	public void setIncomingStaticChannels(List<StaticChannel> incoming) {
		incomingStaticChannelsProperty().set(FXCollections.observableList(incoming));
	}

	@Cascading
	public ListProperty<StaticChannel> incomingStaticChannelsProperty() {
		return incomingStaticChannels;
	}

	// Outgoing

	private ListProperty<StaticChannel> outgoingStaticChannels = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<StaticChannel> getOutgoingStaticChannels() {
		return outgoingStaticChannelsProperty().get();
	}

	public void setOutgoingStaticChannels(List<StaticChannel> outgoing) {
		outgoingStaticChannelsProperty().set(FXCollections.observableList(outgoing));
	}

	@Cascading
	public ListProperty<StaticChannel> outgoingStaticChannelsProperty() {
		return outgoingStaticChannels;
	}

	// Incoming dynamic

	private ListProperty<DynamicChannel> incomingDynamicChannels = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<DynamicChannel> getIncomingDynamicChannels() {
		return incomingDynamicChannelsProperty().get();
	}

	public void setIncomingDynamicChannels(List<DynamicChannel> incoming) {
		incomingDynamicChannelsProperty().set(FXCollections.observableList(incoming));
	}

	@Transient
	public ListProperty<DynamicChannel> incomingDynamicChannelsProperty() {
		return incomingDynamicChannels;
	}

	// Outgoing dynamic

	private ListProperty<DynamicChannel> outgoingDynamicChannels = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	public List<DynamicChannel> getOutgoingDynamicChannels() {
		return outgoingDynamicChannelsProperty().get();
	}

	public void setOutgoingDynamicChannels(List<DynamicChannel> outgoing) {
		outgoingDynamicChannelsProperty().set(FXCollections.observableList(outgoing));
	}

	@Transient
	public ListProperty<DynamicChannel> outgoingDynamicChannelsProperty() {
		return outgoingDynamicChannels;
	}

	// Direction

	private IntegerProperty direction = new SimpleIntegerProperty();

	public Integer getDirection() {
		return directionProperty().get();
	}

	public void setDirection(int direction) {
		directionProperty().set(direction);
	}

	public IntegerProperty directionProperty() {
		return direction;
	}

	// Angle

	private DoubleProperty angle = new SimpleDoubleProperty(Math.random() * 360);

	public Double getAngle() {
		return angle.get();
	}

	public void setAngle(double angle) {
		this.angle.set(angle);
	}

	public DoubleProperty angleProperty() {
		return angle;
	}

}
