package de.tum.imomesa.renderer;

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
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;

public class JMEApplication extends JmeToJfxApplication {

	public JMEApplication() {
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
	
	@Override
	public void simpleInitApp() {
		super.simpleInitApp();
		
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.5f));

		DirectionalLight light = new DirectionalLight();
		light.setDirection(new Vector3f(-3f, -5f, -2f));
		light.setColor(ColorRGBA.White.mult(0.5f));

		rootNode.attachChild(createBox("Floor", 100, 0.5f, 100, 0, -0.5f, 0, ColorRGBA.Green));
		rootNode.attachChild(createBox("Box", 1, 1, 1, 0, 1, 0, ColorRGBA.Blue));
		rootNode.attachChild(createSphere("Sphere", 100, 100, 1, 5, 1, 0, ColorRGBA.Red));
		rootNode.attachChild(createCylinder("Cylinder", 100, 100, 1, 2, 10, 1, 0, ColorRGBA.Yellow));
		rootNode.attachChild(createTorus("Torus", 100, 100, 0.25f, 0.75f, 15, 1, 0, ColorRGBA.Pink));
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
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
	}

	private Geometry createBox(String name, float width, float height, float depth, float x, float y, float z, ColorRGBA color) {
		return createGeometry(name, new Box(width, height, depth), x, y, z, color);
	}

	private Geometry createSphere(String name, int zSamples, int radialSamples, float radius, float x, float y, float z, ColorRGBA color) {
		return createGeometry(name, new Sphere(zSamples, radialSamples, radius), x, y, z, color);
	}

	private Geometry createCylinder(String name, int axisSamples, int radialSamples, float radius, float height, float x, float y, float z, ColorRGBA color) {
		return createGeometry(name, new Cylinder(axisSamples, radialSamples, radius, height, true, false), x, y, z, color);
	}

	private Geometry createTorus(String name, int circleSamples, int radialSamples, float innerRadius, float outerRadius, float x, float y, float z, ColorRGBA color) {
		return createGeometry(name, new Torus(circleSamples, radialSamples, innerRadius, outerRadius), x, y, z, color);
	}

	private Geometry createGeometry(String name, Mesh mesh, float x, float y, float z, ColorRGBA color) {
		Geometry geometry = new Geometry(name, mesh);

		geometry.setLocalTranslation(x, y, z);
		geometry.setMaterial(createMaterial(color));

		return geometry;
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

		return material;
	}

}
