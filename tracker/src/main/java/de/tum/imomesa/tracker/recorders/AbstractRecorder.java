package de.tum.imomesa.tracker.recorders;

public abstract class AbstractRecorder extends Thread {
	
	public AbstractRecorder(String name) {
		super(name);
	}
	
	@Override
	public abstract void run();
	
	public abstract void finish();
	
}
