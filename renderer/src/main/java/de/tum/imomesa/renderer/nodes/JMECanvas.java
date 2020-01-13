package de.tum.imomesa.renderer.nodes;

import com.jme3.jfx.injfx.JmeToJfxIntegrator;
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
	
	private JMECanvas() {
		AppSettings settings = JmeToJfxIntegrator.prepareSettings(new AppSettings(true), 60);
		
		application = JMEApplication.getInstance();
		application.setSettings(settings);
		application.setShowSettings(false);
		application.setDisplayFps(false);
		application.setDisplayStatView(false);
		
		JmeToJfxIntegrator.startAndBindMainViewPort(application, this, Thread::new);
	}
	
	public JMEApplication getApplication() {
		return application;
	}
	
}
