package de.tum.imomesa.checker.markers;

import de.tum.imomesa.core.markers.Marker;
import de.tum.imomesa.model.Element;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class SyntacticMarker extends Marker {

	public SyntacticMarker() {
		
	}
	
	public SyntacticMarker(Element source, String message) {
		setObject(source);
		setMessage(message);
		setScope(source.getClass().getSimpleName());
		pathProperty().bind(source.getElementPath());
	}

	// scope
	private StringProperty scope = new SimpleStringProperty();

	public String getScope() {
		return scope.get();
	}

	private void setScope(String s) {
		scope.set(s);
	}

	public StringProperty scopeProperty() {
		return scope;
	}

	// path
	private StringProperty path = new SimpleStringProperty();

	public String getPath() {
		return path.get();
	}

	public void setPath(String s) {
		path.set(s);
	}

	public StringProperty pathProperty() {
		return path;
	}

	// Object
	private ObjectProperty<Element> object = new SimpleObjectProperty<>();

	public Element getElement() {
		return object.get();
	}

	public void setObject(Element object) {
		this.object.set(object);
	}

	public ObjectProperty<Element> objectProperty() {
		return object;
	}

	// Message
	private StringProperty message = new SimpleStringProperty();

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public StringProperty messageProperty() {
		return message;
	}

}
