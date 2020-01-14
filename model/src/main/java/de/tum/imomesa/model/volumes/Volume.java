package de.tum.imomesa.model.volumes;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import de.tum.imomesa.database.annotations.Cascading;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.transforms.Transform;
import de.tum.imomesa.utilities.visitors.Visitor;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.MeshContainer;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.jcsg.Vertex;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
		setMatrix(Transform.toMatrix(getTransforms()));
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
		setMatrix(Transform.toMatrix(getTransforms()));
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
	private final ObjectProperty<eu.mihosoft.vvecmath.Transform> transform = new SimpleObjectProperty<>(eu.mihosoft.vvecmath.Transform.unity());
	
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
	
	// Matrix
	private final ObjectProperty<Matrix4f> matrix = new SimpleObjectProperty<>();
	
	protected void setMatrix(Matrix4f matrix) {
		this.matrix.set(matrix);
	}
	
	public Matrix4f getMatrix() {
		return matrix.get();
	}
	
	@Transient
	public ObjectProperty<Matrix4f> matrixProperty() {
		return matrix;
	}
	
	// CSG
	private final ObjectProperty<CSG> csg = new SimpleObjectProperty<>(CSG.fromPolygons());
	
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
	private final ObjectProperty<MeshContainer> meshes = new SimpleObjectProperty<>();
	
	protected void setMeshes(MeshContainer meshes) {
		this.meshes.set(meshes);
	}
	
	public MeshContainer getMeshes() {
		return meshes.get();
	}
	
	@Transient
	public ObjectProperty<MeshContainer> meshesProperty() {
		return meshes;
	}
	
	// Mesh
	private final ObjectProperty<Spatial> spatial = new SimpleObjectProperty<>();
	
	protected void setSpatial(Spatial spatial) {
		this.spatial.set(spatial);
	}
	
	public Spatial getSpatial() {
		return spatial.get();
	}
	
	@Transient
	public ObjectProperty<Spatial> spatialProperty() {
		return spatial;
	}
	
	// Dirty
	private final BooleanProperty dirty = new SimpleBooleanProperty(true);
	
	private void setDirty(boolean dirty) {
		this.dirty.set(dirty);
	}
	
	public boolean getDirty() {
		return dirty.get();
	}
	
	@Transient
	public BooleanProperty dirtyProperty() {
		return dirty;
	}
	
	public static final Thread UPDATE_THREAD = new Thread(Volume::updateLoop);
	
	private static final BlockingQueue<Volume> UPDATE_QUEUE = new LinkedBlockingDeque<>();
	
	private static void updateLoop() {
		try {
			while (true) {
				try {
					Volume volume = UPDATE_QUEUE.take();
					
					if (volume.getDirty()) {
						CSG csg = volume.toCSG();
						MeshContainer container = csg.toJavaFXMesh();
						Spatial spatial = toSpatial(csg);
						
						Platform.runLater(() -> {
							volume.setCSG(csg);
							volume.setMeshes(container);
							volume.setSpatial(spatial);
							volume.setDirty(false);
						});
					}
				} catch (Exception e) {
					if (e instanceof InterruptedException) {
						throw e;
					} else {
						e.printStackTrace();
					}
				}
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}
	
	protected <T> void updateTransform(ObservableValue<? extends T> property, T oldValue, T newValue) {
		setMatrix(Transform.toMatrix(getTransforms()));
		setTransform(Transform.toVVecMathTransform(getTransforms()));
	}
	
	protected <T> void updateCSG(ObservableValue<? extends T> property, T oldValue, T newValue) {
		try {
			setDirty(true);
			
			UPDATE_QUEUE.put(this);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}
	
	protected abstract CSG toCSG();
	
	public static Spatial toSpatial(CSG csg) {
		Node node = new Node();
		
		List<Polygon> polygons = csg.getPolygons();
		
		polygons.forEach(polygon -> {
			List<Vertex> vertices = polygon.vertices;
					
			Vector3f[] positions = new Vector3f[vertices.size()];
			
			for (int index = 0; index < vertices.size(); index++) {
				Vertex vertex = vertices.get(index);
				
				float x = (float) vertex.pos.getX();
				float y = (float) vertex.pos.getY();
				float z = (float) vertex.pos.getZ();
				
				positions[index] = new Vector3f(x, y, z);
			}
			
			Vector2f[] texCoords = new Vector2f[vertices.size()];
			
			for (int index = 0; index < vertices.size(); index++) {
				texCoords[index] = new Vector2f(0, 0);
			}
			
			Vector3f[] normals = new Vector3f[vertices.size()];
			
			for (int index = 0; index < vertices.size(); index++) {
				float x = (float) polygon.getPlane().getNormal().getX();
				float y = (float) polygon.getPlane().getNormal().getY();
				float z = (float) polygon.getPlane().getNormal().getZ();
				
				normals[index] = new Vector3f(x, y, z);
			}
			
			int[] indices = new int[(vertices.size() - 2) * 3];
			
			for (int index = 0; index < vertices.size() - 2; index++) {
				indices[index * 3 + 0] = 0;
				indices[index * 3 + 1] = index + 1;
				indices[index * 3 + 2] = index + 2;
			}
			
			Mesh mesh = new Mesh();
			
			mesh.setStatic();
			mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
			mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
			mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
			mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
			mesh.updateBound();
			
			Geometry geometry = new Geometry("mesh", mesh);
			
			node.attachChild(geometry);
		});
		
		return node; 
	}
	
}
