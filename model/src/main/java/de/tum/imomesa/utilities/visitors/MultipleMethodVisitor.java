package de.tum.imomesa.utilities.visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.tum.imomesa.model.Element;

public abstract class MultipleMethodVisitor extends Visitor {
	
	@Override
	public <T extends Element> void dispatch(T object) {
		try {
			// Initialize iterator
			Class<?> iterator = object.getClass();
			// Iterate until you find a right method
			while (Element.class.isAssignableFrom(iterator)) {
				try {
					// Obtain method
					Method method = getClass().getMethod("visit", iterator);
					// Invoke method
					method.invoke(this, object);
				}
				catch (NoSuchMethodException e) {
					// do nothing, can occur as it has not to be implemented for all levels
				}
				// Iterate up
				iterator = iterator.getSuperclass();
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
