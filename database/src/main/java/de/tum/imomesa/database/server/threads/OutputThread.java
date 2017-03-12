package de.tum.imomesa.database.server.threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OutputThread extends StoppableThread {

	private File target;
	private OpenOption[] options;
	private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
	
	public OutputThread(File target, OpenOption... options) {
		this.target = target;
		this.options = options;
	}
	
	@Override
	public void run() {
		while (!getStop() || !queue.isEmpty()) {
			try {
				String item = queue.poll(1, TimeUnit.SECONDS);
				if (item != null) {
					//System.out.println("Item: " + item);
					Files.write(target.toPath(), item.getBytes(), options);
				}
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
	public BlockingQueue<String> getQueue() {
		return queue;
	}
	
}