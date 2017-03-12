package de.tum.imomesa.simulator.markers.errors;

import java.io.File;
import java.util.ArrayList;

import de.tum.imomesa.database.annotations.Transient;
import de.tum.imomesa.simulator.markers.ErrorMarker;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeadlockError extends ErrorMarker {

	public DeadlockError() {
		
	}
	
	public DeadlockError(int step, File image) {
		super(new ArrayList<>(), "Deadlock detected!", step);
		
		this.imageFile.set(image);
		this.imagePath.set(image.getAbsolutePath());
	}
	
	private ObjectProperty<File> imageFile = new SimpleObjectProperty<>();
	
	public File getImageFile() {
		return imageFile.get();
	}
	
	@Transient
	public ObjectProperty<File> imageFileProperty() {
		return imageFile;
	}
	
	private StringProperty imagePath = new SimpleStringProperty();
	
	public StringProperty imagePathProperty() {
		return imagePath;
	}

}
