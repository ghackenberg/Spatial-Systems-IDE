package de.tum.imomesa.renderer.applications;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.jfx.injfx.JmeToJfxApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.transforms.RawTransform;
import de.tum.imomesa.model.transforms.RotationTransform;
import de.tum.imomesa.model.transforms.TranslationTransform;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.simulator.Memory;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.jcsg.Vertex;

public class JMEApplication extends JmeToJfxApplication {
	
	private static JMEApplication INSTANCE;
	
	public static JMEApplication getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new JMEApplication();
		}
		return INSTANCE;
	}
	
	private Component<?> component;
	private Scenario scenario;
	private Memory memory;
	private int step;
	private Node geometryNode;

	private JMEApplication() {
		AppStateManager manager = getStateManager();
		
		if (!manager.detach(manager.getState(StatsAppState.class))) {
			throw new IllegalStateException();
		}
		if (!manager.detach(manager.getState(DebugKeysAppState.class))) {
			throw new IllegalStateException();
		}
		if (!manager.detach(manager.getState(FlyCamAppState.class))) {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void setComponent(Component<?> component) {
		this.component = component;
	}
	public Component<?> getComponent() {
		return component;
	}
	
	public synchronized void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	public Scenario getScenario() {
		return scenario;
	}
	
	public synchronized void setMemory(Memory memory) {
		this.memory = memory;
	}
	public Memory getMemory() {
		return memory;
	}
	
	public synchronized void setStep(int step) {
		this.step = step;
	}
	public int getStep() {
		return step;
	}
	
	@Override
	public synchronized void simpleInitApp() {
		super.simpleInitApp();
		
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.5f));

		DirectionalLight light = new DirectionalLight();
		light.setDirection(new Vector3f(-3f, -5f, -2f));
		light.setColor(ColorRGBA.White.mult(0.5f));
		
		geometryNode = new Node();
		
		rootNode.attachChild(geometryNode);
		rootNode.addLight(ambient);
		rootNode.addLight(light);
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.setCullHint(CullHint.Never);

		cam.setLocation(new Vector3f(5f, 15f, 20f));
		cam.lookAt(new Vector3f(7.5f, 0f, 0f), Vector3f.UNIT_Y);

		DirectionalLightShadowRenderer renderer = new DirectionalLightShadowRenderer(getAssetManager(), 2048, 4);
		renderer.setShadowIntensity(0.2f);
		renderer.setRenderBackFacesShadows(false);
		renderer.setLight(light);

		DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(getAssetManager(), 2048, 4);
		filter.setShadowIntensity(0.2f);
		filter.setRenderBackFacesShadows(false);
		filter.setLight(light);
		filter.setEnabled(true);

		CartoonEdgeFilter cartoon = new CartoonEdgeFilter();
		cartoon.setEdgeColor(ColorRGBA.White);

		FilterPostProcessor processor = new FilterPostProcessor(getAssetManager());
		processor.addFilter(filter);
		processor.addFilter(cartoon);
		
		getViewPort().setBackgroundColor(ColorRGBA.White);
		getViewPort().addProcessor(renderer);
		getViewPort().addProcessor(processor);
	}
	
	@Override
	public synchronized void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		
		Component<?> component = getComponent();
		
		if (component != null) {			
			geometryNode.getChildren().clear();
			
			float camX = component.getCameraTranslateX().floatValue();
			float camY = component.getCameraTranslateY().floatValue();
			float camZ = component.getCameraTranslateZ().floatValue();
			
			Vector3f from = new Vector3f(camX, camY, camZ);
			Vector3f to = new Vector3f(0, 0, 0);
			
			cam.setLocation(from);
			cam.lookAt(to, Vector3f.UNIT_Y);
			
			geometryNode.attachChild(convert(component));
		}
	}
	
	private Spatial convert(Component<?> component) {
		if (component instanceof DefinitionComponent) {
			return convert((DefinitionComponent) component);
		} else if (component instanceof ReferenceComponent) {
			return convert((ReferenceComponent) component);
		} else {
			throw new IllegalStateException();
		}
	}
	
	private Spatial convert(DefinitionComponent component) {
		Node node = new Node();
		
		component.getComponents().forEach(child -> {
			node.attachChild(convert(child));
		});
		component.getParts().forEach(part -> {
			node.attachChild(convert(part));
		});
		node.setLocalTransform(convert(component.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(ReferenceComponent component) {
		Node node = new Node();
		
		node.attachChild(convert(component.getTemplate()));
		node.setLocalTransform(convert(component.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(Part part) {
		Node node = new Node();

		float r = part.getRed() / 255f;
		float g = part.getGreen() / 255f;
		float b = part.getBlue() / 255f;
		float a = part.getVolume().getDirty() ? 0.5f : 1f;
		
		ColorRGBA color = new ColorRGBA(r, g, b, a);
		
		Spatial volume = convert(part.getVolume());
		
		volume.setMaterial(createMaterial(color));
		
		node.attachChild(volume);
		node.setLocalTransform(convert(part.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(Volume volume) {
		return convert(volume.getCSG());
		/*
		if (volume instanceof CompositeVolume) {
			return convert((CompositeVolume) volume);
		} else if (volume instanceof AtomicVolume) {
			return convert((AtomicVolume) volume);
		} else {
			throw new IllegalStateException();
		}
		*/
	}
	
	/*
	private Spatial convert(CompositeVolume volume) {
		Node node = new Node();
		
		volume.getVolumes().forEach(child -> {
			node.attachChild(convert(child));
		});
		node.setLocalTransform(convert(volume.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(AtomicVolume volume) {
		if (volume instanceof BoxVolume) {
			return convert((BoxVolume) volume);
		} else if (volume instanceof SphereVolume) {
			return convert((SphereVolume) volume);
		} else if (volume instanceof CylinderVolume) {
			return convert((CylinderVolume) volume);
		} else {
			throw new IllegalStateException();
		}
	}
	
	private Spatial convert(BoxVolume volume) {
		float width = volume.getWidth().floatValue() / 2;
		float height = volume.getHeight().floatValue() / 2;
		float depth = volume.getDepth().floatValue() / 2;
		
		Box box = new Box(width, height, depth);
		
		Geometry geometry = new Geometry("box", box);
		
		geometry.setLocalTransform(convert(volume.getTransforms()));
		
		return geometry;
	}
	
	private Spatial convert(SphereVolume volume) {
		int slices = volume.getSlices();
		int stacks = volume.getStacks();
		float radius = (float) volume.getRadius();
		
		Sphere sphere = new Sphere(slices, stacks, radius);
		
		Geometry geometry = new Geometry("sphere", sphere);
		
		geometry.setLocalTransform(convert(volume.getTransforms()));
		
		return geometry;
	}
	
	private Spatial convert(CylinderVolume volume) {
		int slices = volume.getSlices();
		float radius = (float) volume.getRadius();
		float height = (float) volume.getHeight();
		
		Cylinder cylinder = new Cylinder(1, slices, radius, height, true, false);
		
		Geometry geometry = new Geometry("cylinder", cylinder);
		
		geometry.setLocalTransform(convert(volume.getTransforms()));
		
		return geometry;
	}
	*/
	
	private Spatial convert(CSG csg) {
		Node node = new Node();
		
		List<Polygon> polygons = csg.getPolygons();
		
		polygons.forEach(polygon -> {
			List<Vertex> vertices = polygon.vertices;
			
			System.out.println(vertices.size());
			
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
			mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
			mesh.updateBound();
			
			Geometry geometry = new Geometry("mesh", mesh);
			
			node.attachChild(geometry);
		});
		
		return node; 
	}
	
	private Transform convert(List<de.tum.imomesa.model.transforms.Transform> transforms) {
		Matrix4f matrix = Matrix4f.IDENTITY;
		
		for (de.tum.imomesa.model.transforms.Transform transform : transforms) {
			matrix = matrix.mult(convert(transform));
		}
		
		Transform result = new Transform();
		
		result.fromTransformMatrix(matrix);
		
		return result;
	}
	
	private Matrix4f convert(de.tum.imomesa.model.transforms.Transform transform) {
		if (transform instanceof TranslationTransform) {
			return convert((TranslationTransform) transform);
		} else if (transform instanceof RotationTransform) {
			return convert((RotationTransform) transform);
		} else if (transform instanceof RawTransform) {
			return convert((RawTransform) transform);
		} else {
			throw new IllegalStateException();
		}
	}
	
	private Matrix4f convert(TranslationTransform transform) {
		Matrix4f result = new Matrix4f();
		
		float x = (float) transform.getVector().getX();
		float y = (float) transform.getVector().getY();
		float z = (float) transform.getVector().getZ();
		
		result.setTranslation(x, y, z);
		
		return result;
	}
	
	private Matrix4f convert(RotationTransform transform) {
		Matrix4f result = new Matrix4f();
		
		float x = (float) transform.getRotationAxe().getX();
		float y = (float) transform.getRotationAxe().getY();
		float z = (float) transform.getRotationAxe().getZ();
		float a = transform.getAngle().floatValue();
		
		result.setRotationQuaternion(new Quaternion(x, y, z, a));
		
		return result;
	}
	
	private Matrix4f convert(RawTransform transform) {
		Matrix4f result = new Matrix4f();
		
		RealMatrix matrix = transform.toRealMatrix(); 
		
		result.m00 = (float) matrix.getEntry(0, 0);
		result.m01 = (float) matrix.getEntry(0, 1);
		result.m02 = (float) matrix.getEntry(0, 2);
		result.m03 = (float) matrix.getEntry(0, 3);

		result.m10 = (float) matrix.getEntry(1, 0);
		result.m11 = (float) matrix.getEntry(1, 1);
		result.m12 = (float) matrix.getEntry(1, 2);
		result.m13 = (float) matrix.getEntry(1, 3);

		result.m20 = (float) matrix.getEntry(2, 0);
		result.m21 = (float) matrix.getEntry(2, 1);
		result.m22 = (float) matrix.getEntry(2, 2);
		result.m23 = (float) matrix.getEntry(2, 3);
		
		result.m30 = (float) matrix.getEntry(3, 0);
		result.m31 = (float) matrix.getEntry(3, 1);
		result.m32 = (float) matrix.getEntry(3, 2);
		result.m33 = (float) matrix.getEntry(3, 3);
		
		return result;
	}

	private Material createMaterial(ColorRGBA color) {
		Texture texture = assetManager.loadTexture("Textures/ColorRamp/toon.png");

		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

		material.setTexture("ColorRamp", texture);
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Ambient", color);
		material.setColor("Specular", ColorRGBA.Black);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setBoolean("VertexLighting", true);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

		return material;
	}

}
