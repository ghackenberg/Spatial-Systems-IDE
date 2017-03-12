package de.tum.imomesa.model.executables;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public abstract class Transition<L extends Label> extends NamedElement {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getGuard() != null) {
			getGuard().accept(visitor);
		}
		for (Action a : actions) {
			a.accept(visitor);
		}
	}

	// Source
	private ObjectProperty<L> sourceLabel = new SimpleObjectProperty<>();

	public L getSourceLabel() {
		return sourceLabel.get();
	}

	public void setSourceLabel(L source) {
		this.sourceLabel.set(source);
	}

	public ObjectProperty<L> sourceLabelProperty() {
		return sourceLabel;
	}

	// Target
	private ObjectProperty<L> targetLabel = new SimpleObjectProperty<>();

	public L getTargetLabel() {
		return targetLabel.get();
	}

	public void setTargetLabel(L target) {
		this.targetLabel.set(target);
	}

	public ObjectProperty<L> targetLabelProperty() {
		return targetLabel;
	}

	// Guard
	private ObjectProperty<Guard> guard = new SimpleObjectProperty<>();

	public Guard getGuard() {
		return guard.get();
	}

	public void setGuard(Guard guard) {
		this.guard.set(guard);
	}

	@Cascading
	public ObjectProperty<Guard> guardProperty() {
		return guard;
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

	// Source angle
	private DoubleProperty sourceAngle = new SimpleDoubleProperty(Math.random() * 360);

	public Double getSourceAngle() {
		return sourceAngle.get();
	}

	public void setSourceAngle(double angle) {
		this.sourceAngle.set(angle);
	}

	public DoubleProperty sourceAngleProperty() {
		return sourceAngle;
	}

	// Target angle
	private DoubleProperty targetAngle = new SimpleDoubleProperty(Math.random() * 360);

	public Double getTargetAngle() {
		return targetAngle.get();
	}

	public void setTargetAngle(double angle) {
		this.targetAngle.set(angle);
	}

	public DoubleProperty targetAngleProperty() {
		return targetAngle;
	}

}
