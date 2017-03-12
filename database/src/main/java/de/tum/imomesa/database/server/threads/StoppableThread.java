package de.tum.imomesa.database.server.threads;

public abstract class StoppableThread extends Thread {
	
	private boolean stop = false;
	
	public boolean getStop() {
		return stop;
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
