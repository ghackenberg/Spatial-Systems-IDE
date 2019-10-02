package de.tum.imomesa.database.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tum.imomesa.database.changes.Change;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client {
	
	private Double key = Math.random();
	
	private Service service;

	private Map<Class<?>, Type> types = new HashMap<>();
	private Map<Double, Object> objects = new HashMap<>();
	private Map<Object, Double> keys = new HashMap<>();
	private Map<Object, Map<Object, List<Field>>> references = new HashMap<>();
	private Map<Object, Map<Field, Listener>> listeners = new HashMap<>();
	private ObservableList<Change> changes = FXCollections.observableArrayList();
	
	private Pusher pusher;
	
	public Client(String host, int port) {
		this.service = new Service(host, port, new Socket(this));
		this.pusher = new Pusher();
	}
	
	public void start() throws Exception {
		service.start();
		pusher.start();
		
		// Create request
		Request request = service.createRequest();
		request.method(HttpMethod.GET);
		
		// Send request
		ContentResponse response = request.send();
		
		// Parse response
		JSONObject json = new JSONObject(response.getContentAsString());
		JSONArray json_objects = json.getJSONArray("objects");
		
		// Create instances
		for (int index = 0; index < json_objects.length(); index++) {
			//System.out.print("Loading object ");
			
			JSONObject json_object = json_objects.getJSONObject(index);
			Class<?> type = Class.forName(json_object.getString("type"));
			Double key = json_object.getDouble("key");

			//System.out.print(getType(type).getName() + " (" + key + "): ");
			
			Object instance = type.getConstructor().newInstance();
			objects.put(key, instance);
			keys.put(instance, key);
			
			//System.out.println("Done!");
		}
		
		// Update fields
		for (int index = 0; index < json_objects.length(); index++) {
			JSONObject json_object = json_objects.getJSONObject(index);
			Double key = json_object.getDouble("key");
			Object instance = objects.get(key);
			Type type = getType(instance.getClass());
			JSONObject json_fields = json_object.getJSONObject("fields");
			
			// Process fields
			for (Field field : type.getFields()) {
				// Check if value is available in json
				if (json_fields.has(field.getName())) {
					// Process value
					Object json_value = json_fields.get(field.getName());
					Object java_value = toObject(json_value);
					
					// Update property
					Property<Object> property = field.getValue(instance);
					property.setValue(java_value);
					property.addListener(getListener(instance, field));
					
					// Remember references
					if (hasObject(java_value)) {
						addReference(java_value, instance, field);
					}
    				else if (java_value instanceof Collection) {
    					Collection<?> collection = (Collection<?>) java_value;
    					for (Object nested : collection) {
    						if (hasObject(nested)) {
    							addReference(nested, instance, field);
    						}
    					}
    				}
				} else {
					// Track property
					Property<Object> property = field.getValue(instance);
					property.addListener(getListener(instance, field));
				}
			}
		}
		
		/*
		JSONArray changes = json.getJSONArray("changes");
		
		for (int index = 0; index < changes.length(); index++) {
			//System.out.print("Loading change ");
			
			JSONObject change = changes.getJSONObject(index);
			
			//System.out.print(change.toString() + ": ");
			
			if (change.has("type")) {
				addChange(new ManageChange(change.getDouble("client"), change.getDouble("key"), change.getLong("timestamp"), change.getString("type")));
			}
			else if (change.has("name")) {
				addChange(new UpdateChange(change.getDouble("client"), change.getDouble("key"), change.getLong("timestamp"), change.getString("name"), change.get("value")));
			}
			else {
				addChange(new ReleaseChange(change.getDouble("client"), change.getDouble("key"), change.getLong("timestamp")));
			}
			
			//System.out.println("Done!");
		}
		*/
	}
	
	public void stop() throws Exception {
		pusher.setStop(true);
		pusher.join();
		service.stop();
	}
	
	public Double getKey() {
		return key;
	}
	
	public Service getService() {
		return service;
	}
	
	public Pusher getPusher() {
		return pusher;
	}
	
	public int getObjectCount() {
		return objects.size();
	}
	
	public boolean hasObject(Object object) {
		return keys.containsKey(object);
	}
	
	public boolean hasKey(Double key) {
		return objects.containsKey(key);
	}
	
	public void addChange(Change change) {
		changes.add(0, change);
	}
	
	public ObservableList<Change> getChanges() {
		return changes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ObservableList<T> getObjects(Class<T> type) {
		ObservableList<T> result = FXCollections.observableArrayList();
		for (Entry<Double, Object> entry : objects.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				result.add((T) entry.getValue());
			}
		}
		return result;
	}
	
	public Object getObject(Double key) {
		if (objects.containsKey(key)) {
			return objects.get(key);
		}
		throw new IllegalStateException();
	}
	
	public Double getKey(Object object) {
		if (keys.containsKey(object)) {
			return keys.get(object);
		}
		throw new IllegalStateException(object.toString());
	}
	
	public Type getType(Class<?> type) {
		if (!types.containsKey(type)) {
			types.put(type, new Type(type));
		}
		return types.get(type);
	}
	
	public Listener getListener(Object object, Field field) {
		if (!listeners.containsKey(object)) {
			listeners.put(object, new HashMap<>());
		}
		if (!listeners.get(object).containsKey(field)) {
			listeners.get(object).put(field, new Listener(this, getKey(object), field));
		}
		return listeners.get(object).get(field);
	}
	
	public void removeReference(Object object, Object parent, Field field) {
		List<Field> selected = getReferences(object, parent);
		selected.remove(field);
	}
	
	public void addReference(Object object, Object parent, Field field) {
		List<Field> selected = getReferences(object, parent);
		selected.add(field);
	}
	
	public List<Field> getReferences(Object object, Object parent) {
		Map<Object, List<Field>> selected = getReferences(object);
		if (!selected.containsKey(parent)) {
			selected.put(parent, new ArrayList<>());
		}
		return selected.get(parent);
	}
	
	public Map<Object, List<Field>> getReferences(Object object) {
		if (!references.containsKey(object)) {
			references.put(object, new HashMap<>());
		}
		return references.get(object);
	}
	
	public Double manageObject(Object object) {
		try {
			if (hasObject(object)) {
				return getKey(object);
			}
			
			//System.out.println("Managing object " + object);
			
			Request request;
			String json;
			
			// Parse type
			Type type = getType(object.getClass());
			
			// Create json
			json = "{\"client\":" + getKey() + ",\"type\":\"" + type.getName() + "\"}";
			// Store object on server
			request = service.createRequest();
			request.method(HttpMethod.PUT);
			request.content(new StringContentProvider(json));
			// Parse key
			Double key = Double.valueOf(request.send().getContentAsString());
			
			// Register key/object
			objects.put(key, object);
			keys.put(object, key);
			
			// Store fields
			for (Field field : type.getFields()) {
				// Ontain property
				Property<Object> property = field.getValue(object);
				// Obtain value
				Object value = property.getValue();
				
				// Generate json
				json = "{\"client\":" + getKey() + ",\"key\":" + key + ",\"name\":\"" + field.getName() + "\",\"value\":" + toJSON(value) + "}";
				// Update fields
				request = service.createRequest();
				request.method(HttpMethod.POST);
				request.content(new StringContentProvider(json));
				request.send();
			}

			// Remember references
			for (Field field : type.getFields()) {
				// Obtain property
				Property<Object> property = field.getValue(object);
				// Get value
				Object value = property.getValue();
				
				// Add references
				if (hasObject(value)) {
					addReference(value, object, field);
				}
				else if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					for (Object nested : collection) {
						if (hasObject(nested)) {
							addReference(nested, object, field);
						}
					}
				}
			}
			
			// Add change listeners
			for (Field field : type.getFields()) {
				Property<Object> property = field.getValue(object);
				property.addListener(getListener(object, field));
			}
			
			// Return key
			return key;
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void releaseObject(Object object) {
		try {
			if (!hasObject(object)) {
				throw new IllegalStateException();
			}
			
			// Obtain type
			Type type = getType(object.getClass());
			
			// Process embedded fields
			for (Field field : type.getFields()) {
				// Obtain property
				Property<Object> property = field.getValue(object);
				// Remove listener
				property.removeListener(getListener(object, field));
				// Obtain value
				Object value = property.getValue();
				
				// Remove references
				if (hasObject(value)) {
					removeReference(value, object, field);
				}
				else if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					for (Object nested: collection) {
						if (hasObject(nested)) {
							removeReference(nested, object, field);
						}
					}
				}
			}
			
			// Process embedded fields
			for (Field field : type.getCascadingFields()) {
				// Obtain property
				Property<Object> property = field.getValue(object);
				// Obtain value
				Object value = property.getValue();
				
				// Check null and release
				if (value != null) {
					if (value instanceof Collection) {
						Collection<?> collection = (Collection<?>) value;
						for (Object nested: collection) {
							releaseObject(nested);
						}
					}
					else {
						releaseObject(value);
					}
				}
			}
			
			// Update references
			for (Entry<Object, List<Field>> entry : getReferences(object).entrySet()) {
				Object parent = entry.getKey();
				while (!entry.getValue().isEmpty()) {
					Property<Object> property = entry.getValue().remove(0).getValue(parent);
					Object value = property.getValue();
					if (value instanceof Collection) {
						Collection<?> collection = (Collection<?>) value;
						collection.remove(object);
					}
					else {
						property.setValue(null);
					}
				}
			}
			
			// Obtain key
			Double key = getKey(object);
			
			// Send delete message
			Request request = service.createRequest();
			request.method(HttpMethod.DELETE);
			request.content(new StringContentProvider("{\"client\":" + getKey() + ",\"key\":" + key +"}"));
			request.send();
			
			// Remove from indices
			objects.remove(key);
			keys.remove(object);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void addObject(Object object, Double key) {
		if (hasObject(object)) {
			throw new IllegalStateException();
		}
		
		objects.put(key, object);
		keys.put(object, key);
	}
	
	public void removeObject(Object object) {
		try {
			if (!hasObject(object)) {
				throw new IllegalStateException();
			}
			
			// Obtain type
			Type type = getType(object.getClass());
			
			// Process fields
			for (Field field : type.getFields()) {
				// Obtain property
				Property<Object> property = field.getValue(object);
				// Remove listener
				property.removeListener(getListener(object, field));
				// Obtain value
				Object value = property.getValue();
				
				// Remove references
				if (hasObject(value)) {
					removeReference(value, object, field);
				}
				else if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					for (Object nested: collection) {
						if (hasObject(nested)) {
							removeReference(nested, object, field);
						}
					}
				}
			}
			
			// Obtain key
			Double key = getKey(object);
			
			// Update indices
			objects.remove(key);
			keys.remove(object);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Object toObject(Object value) {
		if (value instanceof Boolean) {
			return value;
		}
		else if (value instanceof Number) {
			return value;
		}
		else if (value instanceof String) {
			return value;
		}
		else if (value instanceof JSONArray) {
			JSONArray array = (JSONArray) value;
			ObservableList<Object> list = FXCollections.observableArrayList();
			for (int index = 0; index < array.length(); index++) {
				list.add(toObject(array.get(index)));
			}
			return list;
		}
		else if (value instanceof JSONObject) {
			JSONObject object = (JSONObject) value;
			if (object.has("key")) {
				return objects.get(object.getDouble("key"));
			}
			else {
				try {
					return Class.forName(object.getString("type"));
				}
				catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
		else if (value.getClass().getName().equals("org.json.JSONObject$Null")) {
			return null;
		}
		else {
			throw new IllegalStateException(value.getClass().getName());
		}
	}
	
	public String toJSON(Object value) {
		if (value instanceof Boolean) {
			return value.toString();
		}
		else if (value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else if (value instanceof Class) {
			Class<?> type = (Class<?>) value; 
			return "{\"type\":\"" + type.getName() + "\"}";
		}
		else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			
			String json = "[";
			for (Object nested : collection) {
				json += (json.length() > 1 ? "," : "") + toJSON(nested);
			}
			json += "]";
			
			return json;
		}
		else if (value != null) {
			Double key;
			if (hasObject(value)) {
				key = getKey(value);
			}
			else {
				key = manageObject(value);
			}
			return "{\"key\":" + key + "}";
		}
		else {
			return "null";
		}
	}
	
}