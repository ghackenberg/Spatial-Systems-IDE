package de.tum.imomesa.database.server.threads;

import java.io.IOException;
import java.util.List;

import de.tum.imomesa.database.server.Socket;

public class HeartbeatThread extends StoppableThread {

	private static final long SECONDS = 10;
	private static final long MINUTES = 0;
	
	private static final long DURATION = 1000 * SECONDS + 1000 * 60 * MINUTES;
	
	private List<Socket> sockets;
	
	public HeartbeatThread(List<Socket> sockets) {
		this.sockets = sockets;
	}

	@Override
	public void run() {
		// Repeat until stopped
		while (!getStop()) {
			try {
				// Send message to each client
				for (Socket socket : sockets) {
					try {
						socket.getSession().getRemote().sendString("");
						// Notifiy heartbeat
						// System.out.println("Heartbeat sent!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Sleep for predefined duration
				Thread.sleep(DURATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
