package de.tum.imomesa.tracker.recorders;

import java.awt.image.BufferedImage;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.tracker.events.RecorderEndEvent;
import de.tum.imomesa.tracker.events.RecorderStartEvent;
import de.tum.imomesa.tracker.structures.Frame;
import de.tum.imomesa.tracker.writers.AbstractWriter;

public abstract class VideoRecorder extends AbstractRecorder {
	
	private boolean running = true;
	private AbstractWriter writer;
	
	public VideoRecorder(String name, AbstractWriter writer) {
		super(name);
		
		this.writer = writer;
	}
	
	@Override
	public final void run() {
		try {
			EventBus.getInstance().publish(new RecorderStartEvent(this));
			
			// Start timestamp
			long timestamp_start = System.currentTimeMillis();
			// Previous timestamp
			long timestamp_previous = timestamp_start;
			// Frame number
			long frame_number = 0;
			// Capture video
			while (running) {
				/*
				// Debug output
				System.out.println(this + " recording new frame");
				*/
				// Obtain image
				BufferedImage image = grabImage();
				// Check image
				if (image != null) {
					// Get timestamp
					long timestamp_current = System.currentTimeMillis();
					// Grab image
					writer.getFrames().put(new Frame(timestamp_previous - timestamp_start, timestamp_current - timestamp_previous, frame_number++, image));
					// Update timestamp
					timestamp_previous = timestamp_current;
				} else {
					// Notify error!
					throw new IllegalStateException();
				}
			}
			// Add null frame
			writer.getFrames().put(new Frame());
			
			EventBus.getInstance().publish(new RecorderEndEvent(this));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void finish() {
		// Mark finished
		running = false;
	}
	
	protected abstract BufferedImage grabImage();
	
}
