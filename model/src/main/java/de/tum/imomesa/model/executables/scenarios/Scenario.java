package de.tum.imomesa.model.executables.scenarios;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.executables.Executable;
import de.tum.imomesa.model.ports.LifeMaterialPort;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class Scenario extends Executable<Step, Transition> {

	@Override
	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (LifeMaterialPort p : ports) {
			p.accept(visitor);
		}
	}

	// Ports
	private ListProperty<LifeMaterialPort> ports = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<LifeMaterialPort> getPorts() {
		return ports.get();
	}

	public void setPorts(List<LifeMaterialPort> ports) {
		this.ports.set(FXCollections.observableList(ports));
	}

	@Cascading
	public ListProperty<LifeMaterialPort> portsProperty() {
		return ports;
	}

	// Final
	private ObjectProperty<Step> finalLabel = new SimpleObjectProperty<>();

	public Step getFinalLabel() {
		return finalLabel.get();
	}

	public void setFinalLabel(Step finalLabel) {
		this.finalLabel.set(finalLabel);
	}

	public ObjectProperty<Step> finalLabelProperty() {
		return finalLabel;
	}

}
