package de.tum.imomesa.model.expressions.booleans.compares;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.expressions.AtomicExpression;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class ObservationCardinalityCompareExpression extends AtomicExpression {
	
	public ObservationCardinalityCompareExpression() {
		super(Boolean.class);
	}

	@Override
	public StringExpression toStringExpression() {
		if (getObservation() != null) {
			// get parent component
			DefinitionComponent parent = getFirstAncestorByType(DefinitionComponent.class);
			// initialize string binding
			StringExpression path = Bindings.concat(getObservation().nameProperty());
			// initialize loop iterator
			Element iterator = getObservation().getParent();
			// loop through element hierarchy
			while (iterator != parent) {
				if (iterator instanceof NamedElement) {
					path = Bindings.concat(((NamedElement) iterator).nameProperty(), ".", path);
				} else {
					path = Bindings.concat(iterator.toString(), ".", path);
				}
				iterator = iterator.getParent();
			}
			// prepend and append remaining parts
			return Bindings.concat(getTypeName() + "(", path, ", Step - ", delayProperty(), ", ", valueProperty(), ")");
		} else {
			return Bindings.concat(getTypeName() + "(<>, Step - ", delayProperty(), ", ", valueProperty(), ")");
		}
	}

	@Override
	public String toString() {
		return getTypeName() + " ( " + getType().getSimpleName() + " )";
	}
	
	protected abstract String getTypeName();
	
	// Value
	private DoubleProperty value = new SimpleDoubleProperty(0.);

	public Double getValue() {
		return value.get();
	}

	public void setValue(Double value) {
		this.value.set(value);
	}

	public DoubleProperty valueProperty() {
		return value;
	}

	// Observation
	private ObjectProperty<Observation> observation = new SimpleObjectProperty<>();

	public Observation getObservation() {
		return observation.get();
	}

	public void setObservation(Observation observation) {
		this.observation.set(observation);
	}

	public ObjectProperty<Observation> observationProperty() {
		return observation;
	}

	// Delay
	private IntegerProperty delay = new SimpleIntegerProperty();

	public Integer getDelay() {
		return delay.get();
	}

	public void setDelay(int delay) {
		this.delay.set(delay);
	}

	public IntegerProperty delayProperty() {
		return delay;
	}
	
}
