package de.tum.imomesa.tracker.events;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.tracker.writers.AbstractWriter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AbstractWriterEvent extends Event {
	
	public AbstractWriterEvent() {
		
	}
	
	public AbstractWriterEvent(AbstractWriter writer) {
		this.writer.set(writer);
	}

	private ObjectProperty<AbstractWriter> writer = new SimpleObjectProperty<>();
	
	public AbstractWriter getWriter() {
		return writer.get();
	}
	
	@Transient
	public ObjectProperty<AbstractWriter> writerProperty() {
		return writer;
	}

}
