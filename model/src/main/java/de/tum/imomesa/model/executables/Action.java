package de.tum.imomesa.model.executables;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.model.Observation;
import de.tum.imomesa.model.expressions.Expression;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Action extends NamedElement {

	public Action() {
		observation.addListener(new ChangeListener<Observation>() {
			@Override
			public void changed(ObservableValue<? extends Observation> observable, Observation oldValue,
					Observation newValue) {
				if (newValue != null) {
					name.bind(observation.get().nameProperty());
				} else {
					name.unbind();
					name.set(null);
				}
			}

		});
	}

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		if (getExpression() != null) {
			getExpression().accept(visitor);
		}
	}

	// Name
	private StringProperty name = new SimpleStringProperty();

	@Override
	@Transient
	public StringProperty nameProperty() {
		return name;
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

	// Expression
	private ObjectProperty<Expression> expression = new SimpleObjectProperty<>();

	public Expression getExpression() {
		return expression.get();
	}

	public void setExpression(Expression expression) {
		this.expression.set(expression);
	}

	@Cascading
	public ObjectProperty<Expression> expressionProperty() {
		return expression;
	}
}
