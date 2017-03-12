package de.tum.imomesa.analyzer.descriptors;

public class EventDescriptor extends Descriptor {

	private long timestamp;
	private String message;
	private String description;

	public EventDescriptor(long timestamp, String message, String description) {
		this.timestamp = timestamp;
		this.message = message;
		this.description = description;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}
	
	public String getDescription() {
		return description;
	}

}
