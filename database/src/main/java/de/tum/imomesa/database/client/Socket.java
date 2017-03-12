package de.tum.imomesa.database.client;

import java.util.Collection;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import de.tum.imomesa.database.changes.ManageChange;
import de.tum.imomesa.database.changes.ReleaseChange;
import de.tum.imomesa.database.changes.UpdateChange;
import javafx.application.Platform;
import javafx.beans.property.Property;

@WebSocket
public class Socket {

	private Client client;
	private Session session;

	public Socket(Client client) {
		this.client = client;
	}

	public Session getSession() {
		return session;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
	}

	@OnWebSocketMessage
	public void onMessage(String message) {
		try {
			// System.out.println("Message: \"" + message + "\" (" + message.length() + ")");
			
			if (message.length() == 0) {
				// Notify heartbeat
				// System.out.println("Hearbeat recieved!");
			} else {
				// Parse message
				JSONObject json = new JSONObject(message);
	
				if (json.has("type")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								// Remeber change
								client.addChange(new ManageChange(json.getDouble("client"), json.getDouble("key"),
										json.getLong("timestamp"), Class.forName(json.getString("type"))));
							} catch (ClassNotFoundException e) {
								throw new IllegalStateException(e);
							}
						}
					});
				} else if (json.has("name")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// Add change
							client.addChange(new UpdateChange(json.getDouble("client"), json.getDouble("key"),
									json.getLong("timestamp"), json.getString("name"), json.get("value")));
						}
					});
				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// Add change
							client.addChange(new ReleaseChange(json.getDouble("client"), json.getDouble("key"),
									json.getLong("timestamp")));
						}
					});
				}
	
				// Compare client
				if (json.getDouble("client") != client.getKey()) {
					// Parse data fields
					Double key = json.getDouble("key");
	
					// Check json
					if (json.has("type")) {
						// Obtain type
						Class<?> type = Class.forName(json.getString("type"));
						// Create instance
						Object instance = type.newInstance();
						// Remember instance
						client.addObject(instance, key);
					} else {
						// Get data
						Object object = client.getObject(key);
						Type type = client.getType(object.getClass());
	
						// Check json
						if (json.has("name")) {
							// Parse data fields
							String name = json.getString("name");
							Object value = json.get("value");
							if (type.hasField(name)) {
								Field field = type.getField(name);
								Listener listener = client.getListener(object, field);
	
								// Get property
								Property<Object> property = field.getValue(object);
								// Obtain old value
								Object oldValue = property.getValue();
								// Obtain new value
								Object newValue = client.toObject(value);
	
								// TODO Find solution for analyzer here!
								// Platform.runLater(new Runnable() {
								// @Override
								// public void run() {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										// Remove old references
										if (client.hasObject(oldValue)) {
											client.removeReference(oldValue, object, field);
										} else if (oldValue instanceof Collection) {
											Collection<?> collection = (Collection<?>) oldValue;
											for (Object nested : collection) {
												if (client.hasObject(nested)) {
													client.removeReference(nested, object, field);
												}
											}
										}
	
										// Add new references
										if (client.hasObject(newValue)) {
											client.addReference(newValue, object, field);
										} else if (newValue instanceof Collection) {
											Collection<?> collection = (Collection<?>) newValue;
											for (Object nested : collection) {
												if (client.hasObject(nested)) {
													client.addReference(nested, object, field);
												}
											}
										}
										// Remove listener temporarily
										property.removeListener(listener);
	
										try {
											// Try updating value
											property.setValue(newValue);
										} catch (ClassCastException e) {
											// Report class cast exception
											System.out.println("Database socket: " + e.getMessage() + " (object = " + object
													+ ", property = " + name + ", oldValue = " + oldValue + ", newValue = "
													+ newValue + ")");
										}
	
										// Add listener again
										property.addListener(listener);
									}
								});
								// }
								// });
							} else {
								// System.out.println("Field does not exist anymore:
								// " + type.getName() + "." + name);
							}
						} else {
							// Remove object
							client.removeObject(object);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		session = null;
	}

}
