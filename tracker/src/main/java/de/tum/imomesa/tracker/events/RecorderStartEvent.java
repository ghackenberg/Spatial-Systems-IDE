package de.tum.imomesa.tracker.events;

import de.tum.imomesa.tracker.recorders.AbstractRecorder;

public class RecorderStartEvent extends AbstractRecorderEvent {

	public RecorderStartEvent() {
		
	}
	
	public RecorderStartEvent(AbstractRecorder recorder) {
		super(recorder);
	}

}
