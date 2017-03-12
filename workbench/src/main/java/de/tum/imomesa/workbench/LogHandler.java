package de.tum.imomesa.workbench;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import de.tum.imomesa.core.events.Event;
import de.tum.imomesa.core.events.EventHandler;
import de.tum.imomesa.database.client.Client;
import de.tum.imomesa.database.client.Converter;
import de.tum.imomesa.utilities.managers.StorageManager;

public class LogHandler implements EventHandler {
	
	private File file;
	
	public LogHandler(File file) {
		this.file = file;
	}
	
	public void handle(Event event) {
		try {
			// Get client
			Client client = (StorageManager.getInstance() != null) ? StorageManager.getInstance().getClient() : null;
			// Write event
			Files.write(file.toPath(), (Converter.convert(client, event).toString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
