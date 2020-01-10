package de.tum.imomesa.renderer.nodes;

import com.jme3.jfx.injfx.JmeToJfxIntegrator;
import com.jme3.system.AppSettings;

import de.tum.imomesa.renderer.JMEApplication;
import javafx.scene.canvas.Canvas;

public class JMECanvas extends Canvas {

	private JMEApplication jme;
	
	public JMECanvas() {
		AppSettings settings = JmeToJfxIntegrator.prepareSettings(new AppSettings(true), 60);
		
		jme = new JMEApplication();
		jme.setSettings(settings);
		jme.setShowSettings(false);
		jme.setDisplayFps(false);
		jme.setDisplayStatView(false);
		
		JmeToJfxIntegrator.startAndBindMainViewPort(jme, this, Thread::new);
	}
	
}
