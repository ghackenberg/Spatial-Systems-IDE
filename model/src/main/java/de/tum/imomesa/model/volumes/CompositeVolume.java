package de.tum.imomesa.model.volumes;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;
import eu.mihosoft.jcsg.CSG;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

public class CompositeVolume extends Volume {

	public CompositeVolume() {
		volumes.addListener(this::handleVolumesChange);
		volumes.addListener(this::handleVolumesItemChange);
		
		operator.addListener(this::updateCSG);
	}

	private void handleVolumesChange(ObservableValue<? extends List<Volume>> property, List<Volume> oldValue, List<Volume> newValue) {
		oldValue.forEach(volume -> {
			volume.csgProperty().removeListener(this::updateCSG);
		});
		newValue.forEach(volume -> {
			volume.csgProperty().addListener(this::updateCSG);
		});
		setCSG(toCSG());
	}

	private void handleVolumesItemChange(ListChangeListener.Change<? extends Volume> change) {
		while (change.next()) {
			change.getRemoved().forEach(volume -> {
				volume.csgProperty().removeListener(this::updateCSG);
			});
			change.getAddedSubList().forEach(volume -> {
				volume.csgProperty().addListener(this::updateCSG);
			});
		}
		setCSG(toCSG());
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Volume v : volumes) {
			v.accept(visitor);
		}
	}
	
	// Operator
	private final IntegerProperty operator = new SimpleIntegerProperty(0);
	
	public int getOperator() {
		return operator.get();
	}
	
	public void setOperator(int operator) {
		this.operator.set(operator);
	}
	
	public IntegerProperty operatorProperty() {
		return operator;
	}

	// Volumes
	private final ListProperty<Volume> volumes = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Volume> getVolumes() {
		return volumes.get();
	}

	public void setVolumes(List<Volume> volumes) {
		this.volumes.set(FXCollections.observableList(volumes));
	}

	@Cascading
	public ListProperty<Volume> volumesProperty() {
		return volumes;
	}

	protected CSG toCSG() {
		CSG result = null;

		for (Volume volume : volumes) {
			if (result == null) {
				result = volume.getCSG();
			} else {
				if (getOperator() == 0) {
					result = result.union(volume.getCSG());
				} else if (getOperator() == 1) {
					result = result.intersect(volume.getCSG());
				} else if (getOperator() == 2) {
					result = result.difference(volume.getCSG());	
				} else {
					throw new IllegalStateException();
				}
			}
		}

		return result.transformed(Transform.toVVecMathTransform(getTransforms()));
	}

}
