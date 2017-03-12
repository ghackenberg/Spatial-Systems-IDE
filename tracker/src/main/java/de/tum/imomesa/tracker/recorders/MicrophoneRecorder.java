package de.tum.imomesa.tracker.recorders;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.tum.imomesa.core.events.EventBus;
import de.tum.imomesa.tracker.events.RecorderEndEvent;
import de.tum.imomesa.tracker.events.RecorderStartEvent;

public class MicrophoneRecorder extends AbstractRecorder {

	private TargetDataLine audio_line;
	private File output;
	
	public MicrophoneRecorder(File output) {
		super("Microphone recorder");
		
		try {
			this.output = output;
			
			output.getParentFile().mkdirs();
			
			// Creating audio format
			AudioFormat audio_format = new AudioFormat(16000, 8, 2, true, true);
			
			// Obtaining audio info
	        DataLine.Info audio_info = new DataLine.Info(TargetDataLine.class, audio_format);
	        if (!AudioSystem.isLineSupported(audio_info)) {
	            System.out.println("Line not supported");
	        }
	        
	        // Creating audio line
	        audio_line = (TargetDataLine) AudioSystem.getLine(audio_info);
	        audio_line.open(audio_format);
	        audio_line.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			EventBus.getInstance().publish(new RecorderStartEvent(this));
			
	        // Creating audio input
	        AudioInputStream audio_input = new AudioInputStream(audio_line);
	
	        // Writing audio output
	        AudioSystem.write(audio_input, AudioFileFormat.Type.WAVE, output);
	        
	        EventBus.getInstance().publish(new RecorderEndEvent(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void finish() {
		// Close audio line
		audio_line.stop();
		audio_line.close();
	}

}
