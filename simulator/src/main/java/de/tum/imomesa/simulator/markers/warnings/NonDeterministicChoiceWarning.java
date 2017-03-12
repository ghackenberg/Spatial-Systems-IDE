package de.tum.imomesa.simulator.markers.warnings;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.executables.Label;
import de.tum.imomesa.model.executables.Transition;
import de.tum.imomesa.simulator.markers.WarningMarker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class NonDeterministicChoiceWarning<L extends Label, T extends Transition<L>, E extends Executable<L, T>>
		extends WarningMarker {

	public NonDeterministicChoiceWarning() {

	}

	public NonDeterministicChoiceWarning(List<Element> context, E executable, L label, List<T> transitions, int size,
			int step) {
		super(context,
				"Non-deterministic choice in \"" + executable.getName() + "." + label.getName() + "\": " + transitions,
				step);

		this.executable.set(executable);
		this.label.set(label);
		this.transitions.set(FXCollections.observableList(transitions));
		this.size.set(size);
	}

	private ObjectProperty<E> executable = new SimpleObjectProperty<>();

	public ObjectProperty<E> executableProperty() {
		return executable;
	}

	private ObjectProperty<L> label = new SimpleObjectProperty<>();

	public ObjectProperty<L> labelProperty() {
		return label;
	}

	private ListProperty<T> transitions = new SimpleListProperty<>(FXCollections.observableArrayList());

	public ListProperty<T> transitionsProperty() {
		return transitions;
	}

	private IntegerProperty size = new SimpleIntegerProperty();

	public IntegerProperty sizeProperty() {
		return size;
	}

	/*
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}

		NonDeterministicChoiceWarning<?, ?, ?> marker = (NonDeterministicChoiceWarning<?, ?, ?>) other;

		if (executable.get() != null && marker.executable.get() != null
				&& !executable.get().equals(marker.executable.get())) {
			return false;
		}
		if (label.get() != null && marker.label.get() != null && !label.get().equals(marker.label.get())) {
			return false;
		}
		if (transitions.get() != null && marker.transitions.get() != null
				&& !transitions.get().equals(marker.transitions.get())) {
			return false;
		}

		return true;
	}
	*/

}
