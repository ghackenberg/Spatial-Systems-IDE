package de.tum.imomesa.database.server;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tum.imomesa.database.server.threads.HeartbeatThread;
import de.tum.imomesa.database.server.threads.OutputThread;

public class Database {

	private static Database INSTANCE;

	public static void createInstance(File runFolder) {
		if (INSTANCE == null) {
			INSTANCE = new Database(runFolder);
		} else {
			throw new IllegalStateException();
		}
	}

	public static Database getInstance() {
		return INSTANCE;
	}

	private File fileJson;
	private File fileLog;

	private HeartbeatThread heartbeatThread;
	private OutputThread jsonThread;
	private OutputThread logThread;

	private JSONObject json;
	private Double version;
	private JSONArray objects;
	private Map<Double, JSONObject> index = new HashMap<>();

	private List<Socket> sockets = new CopyOnWriteArrayList<>();

	private Database(File runFolder) {
		// Create necessary directories
		if ((runFolder.exists() && !runFolder.isDirectory()) || (!runFolder.exists() && !runFolder.mkdirs())) {
			throw new IllegalStateException("Run folder not usable!");
		}

		// Set files
		fileJson = new File(runFolder, "database.json");
		fileLog = new File(runFolder, "database.log");

		// Create threads
		heartbeatThread = new HeartbeatThread(sockets);
		
		jsonThread = new OutputThread(fileJson, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
		logThread = new OutputThread(fileLog, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.APPEND);

		// Parse json
		try {
			json = new JSONObject(FileUtils.readFileToString(fileJson));
		} catch (Exception e) {
			json = new JSONObject("{version:\"0.1\",objects:[]}");
		}

		version = json.getDouble("version");
		objects = json.getJSONArray("objects");

		// Build index
		for (int i = 0; i < objects.length(); i++) {
			index.put(objects.getJSONObject(i).getDouble("key"), objects.getJSONObject(i));
		}
	}

	public void start() {
		heartbeatThread.start();
		jsonThread.start();
		logThread.start();
	}

	public void stop() {
		jsonThread.setStop(true);
		logThread.setStop(true);
		heartbeatThread.setStop(true);
	}

	public JSONObject getJson() {
		return json;
	}

	public Double getVersion() {
		return version;
	}

	public JSONArray getObjects() {
		return objects;
	}

	public void registerSocket(Socket socket) {
		sockets.add(socket);
	}

	public void unregisterSocket(Socket socket) {
		sockets.remove(socket);
	}

	public Double manageObject(JSONObject message) {
		try {
			synchronized (this) {
				// Parse timestamp
				long timestamp = System.currentTimeMillis();
				// Generate key
				double key = Math.random();

				// Set timestamp
				message.put("timestamp", timestamp);
				// Set key
				message.put("key", key);

				// Write csv
				logThread.getQueue().offer(message.toString() + "\n");

				// Create object
				JSONObject object = new JSONObject(message.toString());
				// Set key
				object.put("key", key);
				// Set fields
				object.put("fields", new JSONObject());
				// Update json
				objects.put(object);
				// Update index
				index.put(key, object);

				// Write json
				jsonThread.getQueue().offer(json.toString(3));

				// Inform sockets
				for (Socket socket : sockets) {
					socket.getSession().getRemote().sendString(message.toString());
				}

				// Return generated key
				return key;
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void updateObject(JSONObject message) {
		try {
			synchronized (this) {
				// Parse timestamp
				long timestamp = System.currentTimeMillis();
				// Parse key
				double key = message.getDouble("key");
				// Parse name
				String name = message.getString("name");
				// Parse value
				Object value = message.get("value");

				// Set timestamp
				message.put("timestamp", timestamp);

				// Write csv file
				logThread.getQueue().offer(message.toString() + "\n");

				// Update original
				index.get(key).getJSONObject("fields").put(name, value);

				// Write json file
				jsonThread.getQueue().offer(json.toString(3));

				// Inform sockets
				for (Socket socket : sockets) {
					socket.getSession().getRemote().sendString(message.toString());
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void releaseObject(JSONObject message) {
		try {
			synchronized (this) {
				// Parse timestamp
				long timestamp = System.currentTimeMillis();
				// Parse data
				double key = message.getDouble("key");

				// Set timetamp
				message.put("timestamp", timestamp);

				// Write csv file
				logThread.getQueue().offer(message.toString() + "\n");

				// Remove object from objects
				for (int i = 0; i < objects.length(); i++) {
					if (objects.getJSONObject(i).getDouble("key") == key) {
						objects.remove(i);
						break;
					}
				}

				// Remove object from index
				index.remove(key);

				// Write json file
				jsonThread.getQueue().offer(json.toString(3));

				// Inform sockets
				for (Socket socket : sockets) {
					socket.getSession().getRemote().sendString(message.toString());
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
