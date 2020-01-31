package de.tum.imomesa.model.volumes;

import java.io.IOException;
import java.nio.file.Paths;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.STL;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class STLVolume extends AtomicVolume {

	public STLVolume() {
		filePath.addListener(this::updateCSG);
	}

	// Depth
	private StringProperty filePath = new SimpleStringProperty();

	public String getFilePath() {
		return filePath.get();
	}

	public void setFilePath(String filePath) {
		this.filePath.set(filePath);
	}

	public StringProperty filePathProperty() {
		return filePath;
	}

	protected CSG toCSG() {
		try {
			if (filePath.get() != null && !filePath.get().equals("")) {
				return STL.file(Paths.get(filePath.get())).transformed(getTransform());
			} else {
				return CSG.fromPolygons();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return CSG.fromPolygons();
		}
	}

}
