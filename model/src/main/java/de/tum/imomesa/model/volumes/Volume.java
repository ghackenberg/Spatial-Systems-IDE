package de.tum.imomesa.model.volumes;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public abstract class Volume extends Element {

	public void accept(Visitor visitor) {
		super.accept(visitor);

		for (Transform t : transforms) {
			t.accept(visitor);
		}
	}

	// Transforms
	private final ListProperty<Transform> transforms = new SimpleListProperty<>(FXCollections.observableArrayList());

	public List<Transform> getTransforms() {
		return transforms.get();
	}

	public void setTransforms(List<Transform> transforms) {
		this.transforms.set(FXCollections.observableList(transforms));
	}

	@Cascading
	public ListProperty<Transform> transformsProperty() {
		return transforms;
	}
	
}
