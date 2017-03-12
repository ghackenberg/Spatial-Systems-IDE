package de.tum.imomesa.database.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;

public class Converter {

	public static JSONObject convert(Client client, Object object) {
		return convert(client, object, new ArrayList<>());
	}

	private static JSONObject convert(Client client, Object object, List<Object> context) {
		JSONObject result = new JSONObject();

		if (context.contains(object)) {
			result.put("hash", object.hashCode());
		} else {
			context.add(object);

			if (client != null && client.hasObject(object)) {
				result.put("key", client.getKey(object));
			} else if (object instanceof Class) {
				result.put("class", ((Class<?>) object).getName());
			} else if (!(object instanceof List)) {
				Type type = (client != null) ? client.getType(object.getClass()) : new Type(object.getClass());

				JSONObject fields = new JSONObject();

				for (Field field : type.getFields()) {
					try {
						Property<Object> property = field.getValue(object);

						if (property.getValue() != null) {
							if (property instanceof ListProperty) {
								List<?> list = (List<?>) property.getValue();

								JSONArray array = new JSONArray();

								for (Object item : list) {
									array.put(convert(client, item, context));
								}

								fields.put(field.getName(), array);
							} else if (property instanceof ObjectProperty) {
								fields.put(field.getName(), convert(client, property.getValue(), context));
							} else {
								fields.put(field.getName(), property.getValue());
							}
						}
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				}

				result.put("type", type.getName());
				result.put("hash", object.hashCode());
				result.put("fields", fields);
			} else {
				throw new IllegalStateException("Object may not be a list!");
			}

			context.remove(object);
		}

		return result;
	}

	public static Object convert(Client client, JSONObject object) throws ClassNotFoundException, JSONException {
		return convert(client, object, new HashMap<>());
	}

	private static Object convert(Client client, JSONObject object, Map<Integer, Object> cache) throws ClassNotFoundException, JSONException {
		if (object.has("key")) {
			if (client != null) {
				return client.getObject(object.getDouble("key"));
			} else {
				throw new IllegalStateException("Client may not be null!");
			}
		} else if (object.has("class")) {
			return Class.forName(object.getString("class"));
		} else if (object.has("type")) {
			if (cache.containsKey(object.getInt("hash"))) {
				return cache.get(object.getInt("hash"));
			} else {
				try {
					Class<?> clazz = Class.forName(object.getString("type"));
					Object instance = clazz.newInstance();
					
					cache.put(object.getInt("hash"), instance);

					JSONObject fields = object.getJSONObject("fields");
					Type type = (client != null) ? client.getType(clazz) : new Type(clazz);
					for (Field field : type.getFields()) {
						Property<Object> value = field.getValue(instance);
						if (fields.has(field.getName())) {
							if (value instanceof ListProperty) {
								@SuppressWarnings("unchecked")
								List<Object> list = (List<Object>) value.getValue();
								JSONArray nested = fields.getJSONArray(field.getName());
								for (int index = 0; index < nested.length(); index++) {
									Object item = nested.get(index);

									if (item instanceof JSONObject) {
										list.add(convert(client, (JSONObject) item, cache));
									} else if (item instanceof JSONArray) {
										throw new IllegalStateException("Type not supported here!");
									} else {
										list.add(item);
									}
								}
							} else if (value instanceof ObjectProperty) {
								JSONObject nested = fields.getJSONObject(field.getName());
								value.setValue(convert(client, nested, cache));
							} else {
								Object nested = fields.get(field.getName());
								value.setValue(nested);
							}
						} else {
							value.setValue(null);
						}
					}

					return instance;
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		} else if (object.has("hash")) {
			return cache.get(object.getInt("hash"));
		} else {
			throw new IllegalStateException("Object fields are missing!");
		}
	}

}
