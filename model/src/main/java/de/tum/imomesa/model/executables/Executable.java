package de.tum.imomesa.model.executables;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public abstract class Executable<L extends Label, T extends Transition<L>> extends NamedElement {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (L l : labels) {
			l.accept(visitor);
		}
		for (T t : transitions) {
			t.accept(visitor);
		}
		for (Variable v : variables) {
			v.accept(visitor);
		}
	}

	// Labels
	private ListProperty<L> labels = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<L> getLabels() {
		return labels.get();
	}

	public void setLabels(List<L> labels) {
		this.labels.set(FXCollections.observableList(labels));
	}

	@Cascading
	public ListProperty<L> labelsProperty() {
		return labels;
	}

	// Initial label
	private ObjectProperty<L> initialLabel = new SimpleObjectProperty<>();

	public L getInitialLabel() {
		return initialLabel.get();
	}

	public void setInitialLabel(L label) {
		initialLabel.set(label);
	}

	public ObjectProperty<L> initialLabelProperty() {
		return initialLabel;
	}

	// Transitions
	private ListProperty<T> transitions = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<T> getTransitions() {
		return transitions.get();
	}

	public void setTransitions(List<T> transitions) {
		this.transitions.set(FXCollections.observableList(transitions));
	}

	@Cascading
	public ListProperty<T> transitionsProperty() {
		return transitions;
	}

	// Variables
	private ListProperty<Variable> variables = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Variable> getVariables() {
		return variables.get();
	}

	public void setVariables(List<Variable> variables) {
		this.variables.set(FXCollections.observableList(variables));
	}

	@Cascading
	public ListProperty<Variable> variablesProperty() {
		return variables;
	}
	
	public List<Transition<?>> getIncomingTransitions(Label label) {
		List<Transition<?>> result = new ArrayList<>();
		
		for (T transition : getTransitions()) {
			if (transition.getTargetLabel() == label) {
				result.add(transition);
			}
		}
		
		return result;
	}
	
	public List<Transition<?>> getOutgoingTransitions(Label label) {
		List<Transition<?>> result = new ArrayList<>();
		
		for (T transition : getTransitions()) {
			if (transition.getSourceLabel() == label) {
				result.add(transition);
			}
		}
		
		return result;
	}
	
	public List<T> getIncomingTransitionsTyped(L label) {
		List<T> result = new ArrayList<>();
		
		for (T transition : getTransitions()) {
			if (transition.getTargetLabel() == label) {
				result.add(transition);
			}
		}
		
		return result;
	}
	
	public List<T> getOutgoingTransitionsTyped(L label) {
		List<T> result = new ArrayList<>();
		
		for (T transition : getTransitions()) {
			if (transition.getSourceLabel() == label) {
				result.add(transition);
			}
		}
		
		return result;
	}

}
