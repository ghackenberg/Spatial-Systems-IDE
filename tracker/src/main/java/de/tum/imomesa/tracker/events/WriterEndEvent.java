package de.tum.imomesa.tracker.events;

import de.tum.imomesa.tracker.writers.AbstractWriter;

public class WriterEndEvent extends AbstractWriterEvent {

	public WriterEndEvent() {
		
	}
	
	public WriterEndEvent(AbstractWriter writer) {
		super(writer);
	}

}
