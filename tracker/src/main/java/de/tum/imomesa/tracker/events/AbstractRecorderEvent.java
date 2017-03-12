package de.tum.imomesa.tracker.events;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.tracker.recorders.AbstractRecorder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class AbstractRecorderEvent extends Event {
	
	public AbstractRecorderEvent() {
		
	}
	
	public AbstractRecorderEvent(AbstractRecorder recorder) {
		this.recorder.set(recorder);
	}
	
	private ObjectProperty<AbstractRecorder> recorder = new SimpleObjectProperty<>();
	
	public AbstractRecorder getRecorder() {
		return recorder.get();
	}
	
	@Transient
	public ObjectProperty<AbstractRecorder> recorderProperty() {
		return recorder;
	}

}
