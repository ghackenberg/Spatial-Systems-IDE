package de.tum.imomesa.workbench.scenes.nodes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Cone extends MeshView {

	public Cone(double radius, double height, int divisions) {
		setRadius(radius);
		setHeight(height);
		setDivisions(divisions);

		radiusProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateMesh();
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateMesh();
			}
		});
		divisionsProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateMesh();
			}
		});

		updateMesh();
	}

	private void updateMesh() {
		
		// Create texture coordinates
		
		float[] texCoords = {
				1, 1,	// index 0
				1, 0,	// index 1
				0, 1,	// index 2
				0, 0	// index 3
			};
		
		// Create points
		
		float[] points = new float[(2 + getDivisions() + 1) * 3];
		
		points[0] = 0;
		points[1] = 0;
		points[2] = 0;
		
		points[3] = 0;
		points[4] = getHeight().floatValue();
		points[5] = 0;
		
		for (int division = 0; division <= getDivisions(); division++) {
			points[6 + division * 3 + 0] = (float) (getRadius() * Math.sin(Math.PI * 2 * division / getDivisions()));
			points[6 + division * 3 + 1] = 0;
			points[6 + division * 3 + 2] = (float) (getRadius() * Math.cos(Math.PI * 2 * division / getDivisions()));
		}
		
		// Create faces

		int[] faces = new int[(getDivisions() * 2) * 6];
		
		for (int division = 0; division < getDivisions(); division++) {
			faces[division * 6 + 0] = 2 + division;
			faces[division * 6 + 1] = 1;
			faces[division * 6 + 2] = 0;
			faces[division * 6 + 3] = 0;
			faces[division * 6 + 4] = 2 + division + 1;
			faces[division * 6 + 5] = 2;
			
			faces[getDivisions() * 6 + division * 6 + 0] = 1;
			faces[getDivisions() * 6 + division * 6 + 1] = 0;
			faces[getDivisions() * 6 + division * 6 + 2] = 2 + division;
			faces[getDivisions() * 6 + division * 6 + 3] = 1;
			faces[getDivisions() * 6 + division * 6 + 4] = 2 + division + 1;
			faces[getDivisions() * 6 + division * 6 + 5] = 2;
		}
		
		// Create mesh
		
		TriangleMesh mesh = new TriangleMesh();
		
		mesh.getPoints().setAll(points);
		mesh.getTexCoords().setAll(texCoords);
		mesh.getFaces().setAll(faces);

		setMesh(mesh);
	}

	// Radius

	private DoubleProperty radius = new SimpleDoubleProperty();

	public Double getRadius() {
		return radiusProperty().get();
	}

	public void setRadius(double radius) {
		radiusProperty().set(radius);
	}

	public DoubleProperty radiusProperty() {
		return radius;
	}

	// Height

	private DoubleProperty height = new SimpleDoubleProperty();

	public Double getHeight() {
		return heightProperty().get();
	}

	public void setHeight(Double height) {
		heightProperty().set(height);
	}

	public DoubleProperty heightProperty() {
		return height;
	}

	// Divisions

	private IntegerProperty divisions = new SimpleIntegerProperty();

	public Integer getDivisions() {
		return divisionsProperty().get();
	}

	public void setDivisions(Integer divisions) {
		divisionsProperty().set(divisions);
	}

	public IntegerProperty divisionsProperty() {
		return divisions;
	}

}
