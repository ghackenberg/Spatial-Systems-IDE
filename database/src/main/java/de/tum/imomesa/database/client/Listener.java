package de.tum.imomesa.database.client;

import java.util.Collection;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Listener implements ChangeListener<Object> {

	private Client client;
	private Double key;
	private Field field;

	public Listener(Client client, Double key, Field field) {
		this.client = client;
		this.key = key;
		this.field = field;
	}

	@Override
	public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
		try {
			// Create json
			String json = "{";
			json += "\"client\":" + client.getKey() + ",";
			json += "\"key\":" + key + ",";
			json += "\"name\":\"" + field.getName() + "\",";
			json += "\"value\":" + client.toJSON(newValue);
			json += "}";

			// Remove old references
			if (client.hasObject(oldValue)) {
				client.removeReference(oldValue, client.getObject(key), field);
			} else if (oldValue instanceof Collection) {
				Collection<?> collection = (Collection<?>) oldValue;
				for (Object nested : collection) {
					if (client.hasObject(nested)) {
						client.removeReference(nested, client.getObject(key), field);
					}
				}
			}

			// Add new references
			if (client.hasObject(newValue)) {
				client.addReference(newValue, client.getObject(key), field);
			} else if (newValue instanceof Collection) {
				Collection<?> collection = (Collection<?>) newValue;
				for (Object nested : collection) {
					if (client.hasObject(nested)) {
						client.addReference(nested, client.getObject(key), field);
					}
				}
			}
			
			// Create and send HTTP request
			Request request = client.getService().createRequest();
			request.method(HttpMethod.POST);
			request.content(new StringContentProvider(json));
			
			client.getPusher().getQueue().add(request);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
