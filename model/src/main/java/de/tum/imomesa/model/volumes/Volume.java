package de.tum.imomesa.model.volumes;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.MeshContainer;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

public abstract class Volume extends Element {

	public Volume() {
		transforms.addListener(this::handleTransformsChange);
		transforms.addListener(this::handleTransformsItemChange);
		
		transform.addListener(this::updateCSG);
	}
	
	private void handleTransformsChange(ObservableValue<? extends List<Transform>> property, List<Transform> oldValue, List<Transform> newValue) {
		for (Transform transform : oldValue) {
			transform.transformProperty().removeListener(this::updateTransform);
		}
		for (Transform transform : newValue) {
			transform.transformProperty().addListener(this::updateTransform);
		}
		setTransform(Transform.toVVecMathTransform(getTransforms()));
	}
	
	private void handleTransformsItemChange(ListChangeListener.Change<? extends Transform> change) {
		while (change.next()) {
			for (Transform transform : change.getRemoved()) {
				transform.transformProperty().removeListener(this::updateTransform);
			}
			for (Transform transform : change.getAddedSubList()) {
				transform.transformProperty().addListener(this::updateTransform);
			}
		}
		setTransform(Transform.toVVecMathTransform(getTransforms()));
	}
	
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
	
	// Transform
	private ObjectProperty<eu.mihosoft.vvecmath.Transform> transform = new SimpleObjectProperty<>(eu.mihosoft.vvecmath.Transform.unity());
	
	protected void setTransform(eu.mihosoft.vvecmath.Transform transform) {
		this.transform.set(transform);
	}
	
	public eu.mihosoft.vvecmath.Transform getTransform() {
		return transform.get();
	}
	
	@Transient
	public ObjectProperty<eu.mihosoft.vvecmath.Transform> transformProperty() {
		return transform;
	}
	
	// CSG
	private ObjectProperty<CSG> csg = new SimpleObjectProperty<>(CSG.fromPolygons());
	
	protected void setCSG(CSG csg) {
		this.csg.set(csg);
	}
	
	public CSG getCSG() {
		return csg.get();
	}
	
	@Transient
	public ObjectProperty<CSG> csgProperty() {
		return csg;
	}
	
	// Meshes
	public ObjectProperty<MeshContainer> meshes = new SimpleObjectProperty<>();
	
	private void setMeshes(MeshContainer meshes) {
		this.meshes.set(meshes);
	}
	
	public MeshContainer getMeshes() {
		return meshes.get();
	}
	
	@Transient
	public ObjectProperty<MeshContainer> meshesProperty() {
		return meshes;
	}
	
	protected <T> void updateTransform(ObservableValue<? extends T> property, T oldValue, T newValue) {
		setTransform(Transform.toVVecMathTransform(getTransforms()));
	}
	
	protected <T> void updateCSG(ObservableValue<? extends T> property, T oldValue, T newValue) {
		setCSG(toCSG());
		setMeshes(getCSG().toJavaFXMesh());
	}
	
	protected abstract CSG toCSG();
	
}
