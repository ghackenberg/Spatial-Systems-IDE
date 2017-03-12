package de.tum.imomesa.tracker.recorders;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import de.tum.imomesa.tracker.writers.AbstractWriter;

public class ScreenRecorder extends VideoRecorder {

	private Rectangle maxBounds;
	private Robot robot;
	
	public ScreenRecorder(AbstractWriter writer) {
		super("Screen recorder", writer);
		
		try {
			
			// Obtain screen objects
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] screens = ge.getScreenDevices();
	
			// Calculate maximum bounds
			maxBounds = new Rectangle();
			for (GraphicsDevice screen : screens) {
				Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
				maxBounds.width += screenBounds.width;
				maxBounds.height = Math.max(maxBounds.height, screenBounds.height);
			}
			
			// Create robot
			robot = new Robot();
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected BufferedImage grabImage() {
		if (robot != null) {
			/*
			// Debug output
			System.out.println("Taking screenshot!");
			*/
			BufferedImage original = robot.createScreenCapture(maxBounds);
			BufferedImage scaled = new BufferedImage(original.getWidth() / 2, original.getHeight() / 2, original.getType());
			
			AffineTransform affine = new AffineTransform();
			affine.scale(0.5, 0.5);
			
			AffineTransformOp operation = new AffineTransformOp(affine, AffineTransformOp.TYPE_BICUBIC);
			operation.filter(original, scaled);
			
			return scaled;
		}
		else {
			return null;
		}
	}

}
