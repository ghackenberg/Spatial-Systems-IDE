package de.tum.imomesa.model;

import java.util.ArrayList;
import java.util.List;

import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.utilities.visitors.Visitor;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Element {

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public List<Element> getPath() {
		List<Element> result = new ArrayList<>();

		Element iterator = this;

		while (iterator != null) {
			result.add(0, iterator);
			iterator = iterator.getParent();
		}

		return result;
	}

	// get element path
	public StringExpression getElementPath() {
		// create result
		StringExpression result = new SimpleStringProperty("");

		// set result
		if (getParent() != null) {
			result = result.concat(getParent().getElementPath());
			result = result.concat("/");
		}

		// add current name
		if (this instanceof NamedElement) {
			result = result.concat(((NamedElement) this).nameProperty());
		} else {
			result = result.concat(toString());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public <T extends Element> T getFirstAncestorByType(Class<T> type) {
		Element parent = this;

		// Iterate over the type hierarchy of the given element
		while (parent != null) {
			// check if type found
			if (type.isAssignableFrom(parent.getClass())) {
				return (T) parent;
			}

			parent = parent.getParent();
		}

		// not found
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Element> T getLastAncestorByType(Class<T> type) {
		if (getParent() != null && getParent().getLastAncestorByType(type) != null) {
			return getParent().getLastAncestorByType(type);
		} else if (type.isAssignableFrom(this.getClass())) {
			return (T) this;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Element> List<T> getAncestorsByType(Class<T> type) {
		Element parent = getParent();
		List<T> result = new ArrayList<>();

		// Iterate over the type hierarchy of the given element
		while (parent != null) {
			// check if type found
			if (parent.getClass().equals(type)) {
				result.add((T) parent);
			}

			parent = parent.getParent();
		}

		// return list with all elements
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> List<T> getAncestorsOrSelfByType(Class<T> type) {
		List<T> result = getAncestorsByType(type);
		
		if (getClass().equals(type)) {
			result.add(0, (T) this);
		}
		
		return result;
	}

	public List<Element> append(List<Element> context) {
		List<Element> newContext = new ArrayList<>(context);
		newContext.add(this);
		return newContext;
	}

	public List<Element> resolve(List<Element> context) {
		List<Element> newContext = new ArrayList<>(context);

		List<Element> remainder = new ArrayList<>();

		Element iterator = this.getParent();

		while (iterator != null) {

			if (newContext.contains(iterator)) {

				while (newContext.size() > 0 && newContext.get(newContext.size() - 1) != iterator) {
					newContext.remove(newContext.size() - 1);
				}

				newContext.addAll(remainder);

				return newContext;
			} else {
				remainder.add(0, iterator);

				iterator = iterator.getParent();
			}
		}

		return remainder;
	}

	public void accept(Visitor visitor) {
		visitor.dispatch(this);
	}
	
	// Element type
	
	private StringProperty className = new ReadOnlyStringWrapper(convert(getClass().getSimpleName()));
	
	public String getClassName() {
		return className.get();
	}
	
	public void setClassName(String className) {
		this.className.set(className);
	}
	
	@Transient
	public StringProperty classNameProperty() {
		return className;
	}

	// parent

	private ObjectProperty<Element> parent = new SimpleObjectProperty<>();

	public Element getParent() {
		return parent.get();
	}

	public void setParent(Element parent) {
		this.parent.set(parent);
	}

	public ObjectProperty<Element> parentProperty() {
		return parent;
	}
	
	// convert
	
	private static String convert(String className) {
		String[] parts = className.split("(?=\\p{Upper})");
		String result = parts[0];
		
		for (int index = 1; index < parts.length; index++) {
			result += " " + parts[index].toLowerCase();
		}
		
		return result;
	}
	
}
