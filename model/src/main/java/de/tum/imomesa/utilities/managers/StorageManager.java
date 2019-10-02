package de.tum.imomesa.utilities.managers;

import java.io.File;
import java.util.List;

import de.tum.imomesa.database.changes.Change;
import de.tum.imomesa.database.client.Client;
import de.tum.imomesa.database.server.Database;
import de.tum.imomesa.database.server.Server;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Workspace;
import javafx.collections.ObservableList;

public class StorageManager {
	
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	
	private static StorageManager INSTANCE;

	public static void createInstance(File runFolder) {
		if (INSTANCE == null) {
			INSTANCE = new StorageManager(runFolder);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public static StorageManager getInstance() {
		return INSTANCE;
	}
	
	private Server server; 
	private Client client;
	private Workspace workspace = null;
	
	private StorageManager(File runFolder) {
		Database.createInstance(runFolder);
		try {
			// Create server
			server = new Server(PORT);
			server.start();
		}
		catch (Exception e) {
			// ignore port already in use
		}
		try {
			// Create client
			client = new Client(HOST, PORT);
			client.start();
			// Query workspaces
			List<Workspace> workspaces = client.getObjects(Workspace.class);
			// Create workspace
			if (workspaces.size() == 0) {
				workspace = new Workspace();
				workspace.setName("New workspace");
				
				client.manageObject(workspace);
			}
			// Select workspace
			else {
				workspace = workspaces.get(0);
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Server getServer() {
		return server;
	}
	
	public Client getClient() {
		return client;
	}
	
	/**
	 * This method persists one element
	 * This method shall be called when creating this specific element
	 * 
	 * Furthermore, this method updates as well already created elements
	 * 
	 * @param element Element that shall be persisted
	 */
	public void manageElement(Element element) {
		client.manageObject(element);
	}
	
	/**
	 * This method deletes one element
	 * This method shall be called when deleting an element
	 * This method calls the DataRemover in order to delete all persisted parts of this element
	 * The called method of the DataRemover depends on the name of the given element
	 * @param element Element that shall be removed from the database
	 */
	public void releaseElement(Element element) {
		client.releaseObject(element);
	}
	
	public Double getKey(Element element) {
		if (client.hasObject(element)) {
			return client.getKey(element);
		}
		else {
			return Integer.valueOf(element.hashCode()).doubleValue();
		}
	}
	
	public boolean hasElement(Element element) {
		return client.hasObject(element);
	}
	
	public Element getElement(Double key) {
		return (Element) client.getObject(key);
	}
	
	public ObservableList<Change> getChanges() {
		return client.getChanges();
	}
	
	/**
	 * This methods returns the number of objects stored in the database.
	 * @return Number of objects in database
	 */
	public int getElementtCount() {
		return client.getObjectCount();
	}
	
	/**
	 * This method returns the locally stored workspace
	 * The workspace is initialized and loaded once during construction of this class from the database
	 * @return the workspace. Null if error occurred previously
	 */
	public Workspace getWorkspace() {
		return workspace;
	}
	
	/**
	 * This method closes the connection from the EntityManager to the database
	 * This method shall be called when the program execution is finished
	 */
	public void close() {
		try {
			client.stop();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
		try {
			server.stop();
		}
		catch (Exception e) {
			// ignore port already in use
		}
		INSTANCE = null;
	}
	
}
