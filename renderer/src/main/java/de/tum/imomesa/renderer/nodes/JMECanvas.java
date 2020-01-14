package de.tum.imomesa.renderer.nodes;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import com.jme3.jfx.injfx.JmeToJfxIntegrator;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import de.tum.imomesa.renderer.applications.JMEApplication;
import javafx.scene.canvas.Canvas;

public class JMECanvas extends Canvas {

	private static JMECanvas INSTANCE;
	
	public static JMECanvas getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new JMECanvas();
		}
		return INSTANCE;
	}
	
	private JMEApplication application;
	
	private Vector2f start;
	
	private Vector3f camera;
	private Vector3f focus;
	private Vector3f up;
	private Vector3f direction;
	private Vector3f orthogonal;
	
	private JMECanvas() {
		AppSettings settings = JmeToJfxIntegrator.prepareSettings(new AppSettings(true), 60);
		
		application = JMEApplication.getInstance();
		application.setSettings(settings);
		application.setShowSettings(false);
		application.setDisplayFps(false);
		application.setDisplayStatView(false);
		
		JmeToJfxIntegrator.startAndBindMainViewPort(application, this, Thread::new);
		
		setOnMousePressed(event -> {
			start = new Vector2f((float) event.getScreenX(), (float) event.getScreenY());
			
			float cameraX = application.getComponent().getCameraTranslateX().floatValue();
			float cameraY = application.getComponent().getCameraTranslateY().floatValue();
			float cameraZ = application.getComponent().getCameraTranslateZ().floatValue();
			
			float focusX = application.getComponent().getFocusTranslateX().floatValue();
			float focusY = application.getComponent().getFocusTranslateY().floatValue();
			float focusZ = application.getComponent().getFocusTranslateZ().floatValue();
			
			float upX = application.getComponent().getUpTranslateX().floatValue();
			float upY = application.getComponent().getUpTranslateY().floatValue();
			float upZ = application.getComponent().getUpTranslateZ().floatValue();
			
			camera = new Vector3f(cameraX, cameraY, cameraZ);
			focus = new Vector3f(focusX, focusY, focusZ);
			up = new Vector3f(upX, upY, upZ);
			
			direction = focus.subtract(camera).normalize();
			orthogonal = direction.cross(up).normalize();
		});
		setOnMouseDragged(event -> {
			Vector2f end = new Vector2f((float) event.getScreenX(), (float) event.getScreenY());
			Vector2f delta = end.subtract(start);
			
			if (event.isPrimaryButtonDown()) {
				Matrix3f rotateX = new Matrix3f();
				Matrix3f rotateY = new Matrix3f();
				
				rotateX.fromAngleAxis(-delta.x / 100, up);
				rotateY.fromAngleAxis(-delta.y / 100, orthogonal);
				
				Vector3f cameraNew = rotateX.mult(rotateY.mult(camera.subtract(focus))).add(focus);
				Vector3f upNew = rotateX.mult(rotateY.mult(up)).normalize();
				
				updateCamera(cameraNew);
				updateUp(upNew);
			} else if (event.isSecondaryButtonDown()) {
				Vector3f cameraNew = camera.add(orthogonal.mult(-delta.x / 10)).add(up.mult(delta.y / 10));
				Vector3f focusNew = focus.add(orthogonal.mult(-delta.x / 10)).add(up.mult(delta.y / 10));
				
				updateCamera(cameraNew);
				updateFocus(focusNew);
			} else {
				throw new IllegalStateException();
			}
		});
		setOnMouseReleased(event -> {
			
		});
		setOnScroll(event -> {
			float cameraX = application.getComponent().getCameraTranslateX().floatValue();
			float cameraY = application.getComponent().getCameraTranslateY().floatValue();
			float cameraZ = application.getComponent().getCameraTranslateZ().floatValue();
			
			float focusX = application.getComponent().getFocusTranslateX().floatValue();
			float focusY = application.getComponent().getFocusTranslateY().floatValue();
			float focusZ = application.getComponent().getFocusTranslateZ().floatValue();
			
			camera = new Vector3f(cameraX, cameraY, cameraZ);
			focus = new Vector3f(focusX, focusY, focusZ);
			
			Vector3f delta = focus.subtract(camera).mult((float) event.getDeltaY() / 1000);
			
			Vector3f cameraNew = camera.add(delta);
			
			updateCamera(cameraNew);
		});
	}
	
	private void updateCamera(Vector3f cameraNew) {
		synchronized (application) {
			if (application.getComponent() != null) {
				application.getComponent().setCameraTranslateX((double) cameraNew.x);
				application.getComponent().setCameraTranslateY((double) cameraNew.y);
				application.getComponent().setCameraTranslateZ((double) cameraNew.z);
			}
		}
	}
	
	private void updateFocus(Vector3f focusNew) {
		synchronized (application) {
			if (application.getComponent() != null) {
				application.getComponent().setFocusTranslateX((double) focusNew.x);
				application.getComponent().setFocusTranslateY((double) focusNew.y);
				application.getComponent().setFocusTranslateZ((double) focusNew.z);
			}
		}
	}
	
	private void updateUp(Vector3f upNew) {
		synchronized (application) {
			if (application.getComponent() != null) {
				application.getComponent().setUpTranslateX((double) upNew.x);
				application.getComponent().setUpTranslateY((double) upNew.y);
				application.getComponent().setUpTranslateZ((double) upNew.z);
			}
		}
	}
	
	public JMEApplication getApplication() {
		return application;
	}
	
}
