package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;

public class HelperCylinder {

	private static final ReadOnlyBooleanWrapper ALWAYS = new ReadOnlyBooleanWrapper(true);

	public static Node addCylinder(List<Element> context, CylinderVolume volume, Color color, Configuration config) {
		boolean selected = config.isElementSelected(context, volume);

		Cylinder fillCylinder, lineCylinder;

		if (volume.getFirstAncestorByType(Part.class) != null) {
			fillCylinder = addCylinder(volume, DrawMode.FILL, CullFace.NONE, color, config, ALWAYS, selected);
			lineCylinder = addCylinder(volume, DrawMode.LINE, CullFace.FRONT, Color.BLACK, config,
					config.showOutlineProperty(), selected);
		} else if (volume.getFirstAncestorByType(Port.class) != null) {
			Port port = volume.getFirstAncestorByType(Port.class);

			selected = config.getSelectedElement().getPath().contains(port);

			if (port.getFirstAncestorByType(Scenario.class) != null) {
				Scenario scenario = port.getFirstAncestorByType(Scenario.class);

				selected = selected || (config.getSelectedElement().getPath().contains(scenario)
						&& config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
			}

			fillCylinder = addCylinder(volume, DrawMode.FILL, CullFace.NONE, color, config,
					new ReadOnlyBooleanWrapper(selected), selected);
			lineCylinder = addCylinder(volume, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK, Color.BLACK,
					config, ALWAYS, selected);
		} else {
			throw new IllegalStateException();
		}

		Group group = new Group();

		group.getChildren().add(fillCylinder);
		group.getChildren().add(lineCylinder);

		return group;
	}

	private static Cylinder addCylinder(CylinderVolume volume, DrawMode mode, CullFace cull, Color color,
			Configuration config, BooleanProperty visible, boolean selected) {
		PhongMaterial material = new PhongMaterial();

		if (color == null) {
			HelperColor.bindColor(material, volume, config, selected);
		} else {
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty())
					.then(selected ? color : color.darker()).otherwise(color));
		}

		Cylinder cylinder = new Cylinder(volume.getRadius(), volume.getHeight(), 10);

		cylinder.setDrawMode(mode);
		cylinder.setCullFace(cull);
		cylinder.setMaterial(material);

		cylinder.visibleProperty().bind(visible);
		cylinder.radiusProperty().bind(volume.radiusProperty());
		cylinder.heightProperty().bind(volume.heightProperty());

		HelperTransform.addTransforms(cylinder, volume.getTransforms());

		return cylinder;
	}
}
