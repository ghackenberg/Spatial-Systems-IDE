package de.tum.imomesa.tracker.events;

import de.tum.imomesa.tracker.writers.AbstractWriter;

public class WriterStartEvent extends AbstractWriterEvent {

	public WriterStartEvent() {
		
	}
	
	public WriterStartEvent(AbstractWriter writer) {
		super(writer);
	}

}
