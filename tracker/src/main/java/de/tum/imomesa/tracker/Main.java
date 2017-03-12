package de.tum.imomesa.tracker;

import java.io.File;

import de.tum.imomesa.tracker.recorders.MicrophoneRecorder;
import de.tum.imomesa.tracker.recorders.ScreenRecorder;
import de.tum.imomesa.tracker.recorders.WebcamRecorder;
import de.tum.imomesa.tracker.writers.MovWriter;


public class Main {
	
	private static final long SECONDS = 30;
	private static final long MINUTES = 0;
	private static final long HOURS = 0;
	
	private static final long TIMEOUT = 1000 * (SECONDS + 60 * (MINUTES + 60 * HOURS)); 

	public static void main(String[] args) {

		try {
			
			System.out.println("Start tracking");
			
			MovWriter webcam_writer = new MovWriter("Webcam mov writer", new File("temp/Webcam.mov"));
			MovWriter screen_writer = new MovWriter("Screen mov writer", new File("temp/Screen.mov"));
			
			MicrophoneRecorder microphone_recorder = new MicrophoneRecorder(new File("temp/Microphone.wav"));
			WebcamRecorder webcam_recorder = new WebcamRecorder(webcam_writer);
			ScreenRecorder screen_recorder = new ScreenRecorder(screen_writer);
			
			System.out.println("Start recording");
			
			webcam_writer.start();
			screen_writer.start();
			
			microphone_recorder.start();
			webcam_recorder.start();
			screen_recorder.start();
		
			Thread.sleep(TIMEOUT);
			
			System.out.println("Finish recording");
			
			microphone_recorder.finish();
			webcam_recorder.finish();
			screen_recorder.finish();

			microphone_recorder.join();
			webcam_recorder.join();
			screen_recorder.join();
			
			webcam_writer.join();
			screen_writer.join();
			
			System.out.println("Stop tracking");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
