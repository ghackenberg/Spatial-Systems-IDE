package de.tum.imomesa.model.expressions;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.components.DefinitionComponent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ObservationExpression extends AtomicExpression {

	public ObservationExpression() {
		super(Object.class);
	}

	public ObservationExpression(Class<?> typeObject) {
		super(typeObject);
	}

	@Override
	public StringExpression toStringExpression() {
		if (observation.get() != null) {
			// get parent component
			DefinitionComponent parent = getFirstAncestorByType(DefinitionComponent.class);
			// initialize string binding
			StringExpression path = Bindings.concat(observation.get().nameProperty());
			// initialize loop iterator
			Element iterator = observation.get().getParent();
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
			return Bindings.concat("_", path, "(Step - ", delayProperty(), ")");
		} else {
			return Bindings.concat("_<>(Step - ", delayProperty(), ")");
		}
	}

	@Override
	public String toString() {
		return "Observation ( " + getType().getSimpleName() + " )";
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