package de.tum.imomesa.tracker.structures;

import java.awt.image.BufferedImage;

public class Frame {
	
	private long timestamp;
	private long duration;
	private long number;
	private BufferedImage image;
	
	public Frame() {
		
	}
	public Frame(long timestamp, long duration, long number, BufferedImage image) {
		this.timestamp = timestamp;
		this.duration = duration;
		this.number = number;
		this.image = image;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public long getDurartion() {
		return duration;
	}
	
	public long getNumber() {
		return number;
	}
	
	public BufferedImage getImage() {
		return image;
	}

}
