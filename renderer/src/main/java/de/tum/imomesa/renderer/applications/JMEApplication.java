package de.tum.imomesa.renderer.applications;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.jfx.injfx.JmeToJfxApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;

import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.components.Component;
import de.tum.imomesa.model.components.DefinitionComponent;
import de.tum.imomesa.model.components.ReferenceComponent;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.simulator.Memory;

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
	private FilterPostProcessor processor;

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
		light.setDirection(new Vector3f(-3f, -5f, -2f).normalize());
		light.setColor(ColorRGBA.White.mult(0.5f));
		
		geometryNode = new Node();
		
		rootNode.attachChild(geometryNode);
		rootNode.addLight(ambient);
		rootNode.addLight(light);
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		rootNode.setCullHint(CullHint.Never);

		cam.setLocation(new Vector3f(5f, 15f, 20f));
		cam.lookAt(new Vector3f(7.5f, 0f, 0f), Vector3f.UNIT_Y);

		DirectionalLightShadowRenderer renderer = new DirectionalLightShadowRenderer(getAssetManager(), 4096, 4);
		renderer.setShadowIntensity(0.2f);
		renderer.setRenderBackFacesShadows(true);
		renderer.setLight(light);

		DirectionalLightShadowFilter shadow = new DirectionalLightShadowFilter(getAssetManager(), 4096, 4);
		shadow.setShadowIntensity(0.2f);
		shadow.setRenderBackFacesShadows(true);
		shadow.setLight(light);
		shadow.setEnabled(true);

		CartoonEdgeFilter cartoon = new CartoonEdgeFilter();
		cartoon.setEdgeColor(ColorRGBA.Black);

		processor = new FilterPostProcessor(getAssetManager());
		processor.addFilter(shadow);
		processor.addFilter(cartoon);
		
		getViewPort().setBackgroundColor(new ColorRGBA(1, 1, 1, 0));
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
		node.setLocalTransform(de.tum.imomesa.model.transforms.Transform.toJMETransform(component.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(ReferenceComponent component) {
		Node node = new Node();
		
		node.attachChild(convert(component.getTemplate()));
		node.setLocalTransform(de.tum.imomesa.model.transforms.Transform.toJMETransform(component.getTransforms()));
		
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
		node.setLocalTransform(de.tum.imomesa.model.transforms.Transform.toJMETransform(part.getTransforms()));
		
		return node;
	}
	
	private Spatial convert(Volume volume) {
		return volume.getSpatial();
	}

	private Material createMaterial(ColorRGBA color) {
		Texture texture = assetManager.loadTexture("Textures/ColorRamp/toon.png");

		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

		material.setTexture("ColorRamp", texture);
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Ambient", color);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setBoolean("VertexLighting", true);

		return material;
	}	

}
