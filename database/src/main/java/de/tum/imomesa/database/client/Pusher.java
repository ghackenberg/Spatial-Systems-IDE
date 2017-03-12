package de.tum.imomesa.database.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.api.Request;

public class Pusher extends Thread {

	private boolean stop = false;
	private BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public BlockingQueue<Request> getQueue() {
		return queue;
	}

	@Override
	public void run() {
		while (!stop || !queue.isEmpty()) {
			try {
				Request request = queue.poll(1, TimeUnit.SECONDS);
				if (request != null) {
					request.send();
				}
			} catch (TimeoutException e) {
				throw new IllegalStateException(e);
			} catch (ExecutionException e) {
				throw new IllegalStateException(e);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

}
