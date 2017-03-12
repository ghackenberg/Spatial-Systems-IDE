package de.tum.imomesa.tracker.recorders;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import de.tum.imomesa.tracker.writers.AbstractWriter;

public class WebcamRecorder extends VideoRecorder {
	
	private Webcam webcam;
	
	public WebcamRecorder(AbstractWriter writer) {
		super("Webcam recorder", writer);
		
		webcam = Webcam.getDefault();
		
		/*
		System.out.print("View sizes:");
		for (int i = 0; i < webcam.getViewSizes().length; i++) {
			System.out.print(" " + webcam.getViewSizes()[i].width + "/" + webcam.getViewSizes()[i].height);
		}
		System.out.println();
		
		System.out.print("Custom view sizes:");
		for (int i = 0; i < webcam.getCustomViewSizes().length; i++) {
			System.out.print(" " + webcam.getCustomViewSizes()[i].width + "/" + webcam.getCustomViewSizes()[i].height);
		}
		System.out.println();
		*/
		
		webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length - 1]);
		webcam.open();
	}
	
	@Override
	public void finish() {
		super.finish();

		webcam.close();
	}

	@Override
	protected BufferedImage grabImage() {
		return webcam.getImage();
	}

}
