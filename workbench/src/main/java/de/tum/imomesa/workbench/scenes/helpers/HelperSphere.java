package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class HelperSphere {

	private static final ReadOnlyBooleanWrapper ALWAYS = new ReadOnlyBooleanWrapper(true);

	public static Node addSphere(List<Element> context, SphereVolume volume, Color color, Configuration config) {
		boolean selected = config.isElementSelected(context, volume);

		// set spheres
		Sphere fillSphere, lineSphere;

		if (volume.getFirstAncestorByType(Part.class) != null) {
			fillSphere = addSphere(volume, DrawMode.FILL, CullFace.NONE, color, config, ALWAYS, selected);
			lineSphere = addSphere(volume, DrawMode.LINE, CullFace.FRONT, Color.BLACK, config,
					config.showOutlineProperty(), selected);
		} else if (volume.getFirstAncestorByType(Port.class) != null) {
			Port port = volume.getFirstAncestorByType(Port.class);

			selected = config.getSelectedElement().getPath().contains(port);

			if (port.getFirstAncestorByType(Scenario.class) != null) {
				Scenario scenario = port.getFirstAncestorByType(Scenario.class);

				selected = selected || (config.getSelectedElement().getPath().contains(scenario)
						&& config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
			}

			fillSphere = addSphere(volume, DrawMode.FILL, CullFace.NONE, color, config,
					new ReadOnlyBooleanWrapper(selected), selected);
			lineSphere = addSphere(volume, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK, Color.BLACK,
					config, ALWAYS, selected);
		} else {
			throw new IllegalStateException();
		}

		// create group
		Group group = new Group();

		group.getChildren().add(fillSphere);
		group.getChildren().add(lineSphere);

		return group;
	}

	private static Sphere addSphere(SphereVolume volume, DrawMode mode, CullFace cull, Color color,
			Configuration config, BooleanProperty visible, boolean selected) {
		PhongMaterial material = new PhongMaterial();

		if (color == null) {
			HelperColor.bindColor(material, volume, config, selected);
		} else {
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty())
					.then(selected ? color : color.darker()).otherwise(color));
		}

		Sphere sphere = new Sphere(volume.getRadius(), 10);

		sphere.setDrawMode(mode);
		sphere.setCullFace(cull);
		sphere.setMaterial(material);

		sphere.radiusProperty().bind(volume.radiusProperty());
		sphere.visibleProperty().bind(visible);

		HelperTransform.addTransforms(sphere, volume.getTransforms());

		return sphere;
	}

}
