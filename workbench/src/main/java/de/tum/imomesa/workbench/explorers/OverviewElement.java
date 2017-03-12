package de.tum.imomesa.workbench.explorers;

import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.NamedElement;
import de.tum.imomesa.workbench.commons.helpers.ImageHelper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class OverviewElement<T extends Element> extends NamedElement {
	
	public OverviewElement() {
		
	}
	
	public OverviewElement(Element parent, Class<T> type, String name) {
		setParent(parent);
		setName(name);
		
		this.type.set(type);
		
		icon.set(ImageHelper.getFolderIcon(type));
	}
	
	public OverviewElement(Element parent, Class<T> type, ListProperty<T> property, String name) {
		setParent(parent);
		setName(name);
		
		this.type.set(type);
		this.property.set(property);
		
		icon.set(ImageHelper.getFolderIcon(type));
	}
	
	private ObjectProperty<Node> icon = new SimpleObjectProperty<>();
	
	public Node getIcon() {
		return icon.get();
	}
	
	@Transient
	public ObjectProperty<Node> iconProperty() {
		return icon;
	}

	private ObjectProperty<Class<T>> type = new SimpleObjectProperty<>();
	
	public Class<?> getType() {
		return type.get();
	}
	
	public ObjectProperty<Class<T>> typeProperty() {
		return type;
	}
	
	private ObjectProperty<ListProperty<T>> property = new SimpleObjectProperty<>();
	
	@Transient
	public ListProperty<? extends T> getProperty() {
		return property.get();
	}
	
	@Transient
	public ObjectProperty<ListProperty<T>> propertyProperty() {
		return property;
	}
	
}
