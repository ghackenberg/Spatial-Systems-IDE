package de.tum.imomesa.tracker.events;

import de.tum.imomesa.tracker.recorders.AbstractRecorder;

public class RecorderEndEvent extends AbstractRecorderEvent {

	public RecorderEndEvent() {
		
	}
	
	public RecorderEndEvent(AbstractRecorder recorder) {
		super(recorder);
	}

}
