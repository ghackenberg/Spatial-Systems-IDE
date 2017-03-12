package de.tum.imomesa.database.model;

import java.util.List;

import de.tum.imomesa.database.annotations.Cascading;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class Person {

	private StringProperty name = new SimpleStringProperty("Default");
	
	public void setName(String name) {
		this.name.set(name);
	}
	public String getName() {
		return name.get();
	}
	public StringProperty nameProperty() {
		return name;
	}
	
	private ObjectProperty<Person> friend = new SimpleObjectProperty<>();
	
	public void setFriend(Person friend) {
		this.friend.set(friend);
	}
	public Person getFriend() {
		return friend.get();
	}
	public ObjectProperty<Person> friendProperty() {
		return friend;
	}
	
	private ListProperty<String> colors = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public void setColors(List<String> colors) {
		this.colors.set(FXCollections.observableList(colors));
	}
	public List<String> getColors() {
		return colors.get();
	}
	public ListProperty<String> colorsProperty() {
		return colors;
	}
	
	private ListProperty<Person> parents = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public void setParents(List<Person> parents) {
		this.parents.set(FXCollections.observableList(parents));
	}
	public List<Person> getParents() {
		return parents.get();
	}
	public ListProperty<Person> parentsProperty() {
		return parents;
	}
	
	private ListProperty<Hobby> hobbies = new SimpleListProperty<>(FXCollections.observableArrayList());
	
	public void setHobbies(List<Hobby> hobbies) {
		this.hobbies.set(FXCollections.observableList(hobbies));
	}
	public List<Hobby> getHobbies() {
		return hobbies.get();
	}
	@Cascading
	public ListProperty<Hobby> hobbiesProperty() {
		return hobbies;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
