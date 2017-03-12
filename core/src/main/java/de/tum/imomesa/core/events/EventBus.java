package de.tum.imomesa.core.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class EventBus {
	
	private static EventBus instance = new EventBus();
	
	public static EventBus getInstance() {
		return instance;
	}
	
	private EventBus() {
		// do nothing
	}

	private List<EventHandler> listeners = new ArrayList<>();
	
	public synchronized void publish(Event event) {
		for (Object listener : listeners) {
			Class<?> type = event.getClass();
			try {
				while (Event.class.isAssignableFrom(type)) {
					try {
						Method method = listener.getClass().getMethod("handle", type);
						method.invoke(listener, event);
						break;
					}
					catch (NoSuchMethodException e) {
						type = type.getSuperclass();
					}
				}
			}
			catch (IllegalAccessException | InvocationTargetException e)  {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void subscribe(EventHandler listener) {
		listeners.add(listener);
	}
	
	public synchronized void unsubscribe(EventHandler listener) {
		listeners.remove(listener);
	}
	
}
