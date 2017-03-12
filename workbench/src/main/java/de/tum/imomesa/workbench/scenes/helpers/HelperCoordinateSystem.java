package de.tum.imomesa.workbench.scenes.helpers;

import de.tum.imomesa.workbench.scenes.nodes.Cone;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class HelperCoordinateSystem {

	private static final double GRID_RADIUS = 0.125;
	private static final double AXE_RADIUS = GRID_RADIUS * 2;
	private static final double AXE_ARROW_RADIUS = AXE_RADIUS * 2;
	private static final double AXE_ARROW_HEIGHT = AXE_ARROW_RADIUS * 2;
	private static final double ORIGIN_RADIUS = AXE_ARROW_RADIUS;
	
	public static Group getCoordinates(BooleanProperty show) {
		Group group = new Group();
		
		group.visibleProperty().bind(show);
		
		// add grid - x direction
		Sphere origin = new Sphere(ORIGIN_RADIUS);
		group.getChildren().add(origin);
		
		// x axe
		Cylinder xAxe = new Cylinder(AXE_RADIUS, 120, 10);
		xAxe.getTransforms().add(new Rotate(90, new Point3D(0, 0, 1)));
		xAxe.setMaterial(new PhongMaterial(Color.RED));
		group.getChildren().add(xAxe);
		
		//Sphere xArrow = new Sphere(1.0);
		Cone xArrow = new Cone(AXE_ARROW_RADIUS, AXE_ARROW_HEIGHT, 10);
		xArrow.getTransforms().add(new Translate(60, 0, 0));
		xArrow.getTransforms().add(new Rotate(-90, new Point3D(0, 0, 1)));
		xArrow.setMaterial(new PhongMaterial(Color.RED));
		group.getChildren().add(xArrow);
		
		// y axe
		Cylinder yAxe = new Cylinder(AXE_RADIUS, 120, 10);
		yAxe.setMaterial(new PhongMaterial(Color.GREEN));
		group.getChildren().add(yAxe);
		
		//Sphere yArrow = new Sphere(1.0);
		Cone yArrow = new Cone(AXE_ARROW_RADIUS, AXE_ARROW_HEIGHT, 10);
		yArrow.getTransforms().add(new Translate(0, 60, 0));
		yArrow.getTransforms().add(new Rotate(0, new Point3D(0, 1, 0)));
		yArrow.setMaterial(new PhongMaterial(Color.GREEN));
		group.getChildren().add(yArrow);

		// z axe
		Cylinder zAxe = new Cylinder(AXE_RADIUS, 120, 10);
		zAxe.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
		zAxe.setMaterial(new PhongMaterial(Color.BLUE));
		group.getChildren().add(zAxe);
		
		//Sphere zArrow = new Sphere(1.0);
		Cone zArrow = new Cone(AXE_ARROW_RADIUS, AXE_ARROW_HEIGHT, 10);
		zArrow.getTransforms().add(new Translate(0, 0, 60));
		zArrow.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
		zArrow.setMaterial(new PhongMaterial(Color.BLUE));
		group.getChildren().add(zArrow);
		
		// Grid in x-direction
		for(float i = -50; i <= 50; i += 10) {
			Cylinder xGrid = new Cylinder(GRID_RADIUS, 100, 10);
			xGrid.getTransforms().add(new Translate(0, 0, i));
			xGrid.getTransforms().add(new Rotate(90, new Point3D(0, 0, 1)));
			group.getChildren().add(xGrid);
		}
		
		/*
		for(float i = 0; i <= 50; i += 10) {
			Cylinder xGrid = new Cylinder(GRID_RADIUS, 100, 10);
			xGrid.getTransforms().add(new Translate(0, i, 0));
			xGrid.getTransforms().add(new Rotate(90, new Point3D(0, 0, 1)));
			group.getChildren().add(xGrid);
		}
		*/
		
		/*
		// Grid in y-direction
		for(float i = 0; i <= 50; i += 10) {
			Cylinder yGrid = new Cylinder(GRID_RADIUS, 50, 10);
			yGrid.getTransforms().add(new Translate(0, 25, i));
			group.getChildren().add(yGrid);
		}
		
		for(float i = 0; i <= 50; i += 10) {
			Cylinder yGrid = new Cylinder(GRID_RADIUS, 50, 10);
			yGrid.getTransforms().add(new Translate(i, 25, 0));
			group.getChildren().add(yGrid);
		}
		*/
		
		/*
		// Grid in z-direction
		for(float i = 0; i <= 50; i += 10) {
			Cylinder zGrid = new Cylinder(GRID_RADIUS, 100, 10);
			zGrid.getTransforms().add(new Translate(0, i, 0));
			zGrid.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
			group.getChildren().add(zGrid);
		}
		*/
		
		for(float i = -50; i <= 50; i += 10) {
			Cylinder zGrid = new Cylinder(GRID_RADIUS, 100, 10);
			zGrid.getTransforms().add(new Translate(i, 0, 0));
			zGrid.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
			group.getChildren().add(zGrid);
		}
		
		// endpoints
		/*
		Sphere xyGridEnd = new Sphere(GRID_RADIUS, 10);
		xyGridEnd.getTransforms().add(new Translate(50, 50, 0));
		group.getChildren().add(xyGridEnd);
		
		Sphere yzGridEnd = new Sphere(GRID_RADIUS, 10);
		yzGridEnd.getTransforms().add(new Translate(0, 50, 50));
		group.getChildren().add(yzGridEnd);
		*/
		
		Sphere xzGrid1End = new Sphere(GRID_RADIUS, 10);
		xzGrid1End.getTransforms().add(new Translate(50, 0, 50));
		group.getChildren().add(xzGrid1End);
		
		Sphere xzGrid2End = new Sphere(GRID_RADIUS, 10);
		xzGrid2End.getTransforms().add(new Translate(50, 0, -50));
		group.getChildren().add(xzGrid2End);

		Sphere xzGrid3End = new Sphere(GRID_RADIUS, 10);
		xzGrid3End.getTransforms().add(new Translate(-50, 0, 50));
		group.getChildren().add(xzGrid3End);

		Sphere xzGrid4End = new Sphere(GRID_RADIUS, 10);
		xzGrid4End.getTransforms().add(new Translate(-50, 0, -50));
		group.getChildren().add(xzGrid4End);
		
		return group;
	}

}
