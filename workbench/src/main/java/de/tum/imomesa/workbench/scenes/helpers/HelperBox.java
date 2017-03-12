package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.executables.scenarios.Scenario;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperBox {

	private static final ReadOnlyBooleanWrapper ALWAYS = new ReadOnlyBooleanWrapper(true);

	public static Node addBox(List<Element> context, BoxVolume volume, Color color, Configuration config) {
		boolean selected = config.isElementSelected(context, volume);

		Box fillBox, lineBox;

		if (volume.getFirstAncestorByType(Part.class) != null) {
			fillBox = addBox(volume, DrawMode.FILL, CullFace.NONE, color, config, ALWAYS, selected);
			lineBox = addBox(volume, DrawMode.LINE, CullFace.FRONT, Color.BLACK, config, config.showOutlineProperty(),
					selected);
		} else if (volume.getFirstAncestorByType(Port.class) != null) {
			Port port = volume.getFirstAncestorByType(Port.class);

			selected = config.getSelectedElement().getPath().contains(port);

			if (port.getFirstAncestorByType(Scenario.class) != null) {
				Scenario scenario = port.getFirstAncestorByType(Scenario.class);

				selected = selected || (config.getSelectedElement().getPath().contains(scenario)
						&& config.getSelectedElement().getFirstAncestorByType(Port.class) == null);
			}

			fillBox = addBox(volume, DrawMode.FILL, CullFace.NONE, color, config, new ReadOnlyBooleanWrapper(selected),
					selected);
			lineBox = addBox(volume, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK, Color.BLACK, config,
					ALWAYS, selected);
		} else {
			throw new IllegalStateException();
		}

		Group group = new Group();

		group.getChildren().add(fillBox);
		group.getChildren().add(lineBox);

		return group;
	}

	private static Box addBox(BoxVolume volume, DrawMode mode, CullFace cull, Color color, Configuration config,
			BooleanProperty visible, boolean selected) {
		PhongMaterial material = new PhongMaterial();

		if (color == null) {
			HelperColor.bindColor(material, volume, config, selected);
		} else {
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty())
					.then(selected ? color : color.darker()).otherwise(color));
		}

		Box box = new Box();

		box.setDrawMode(mode);
		box.setCullFace(cull);
		box.setMaterial(material);

		box.visibleProperty().bind(visible);
		box.widthProperty().bind(volume.widthProperty());
		box.heightProperty().bind(volume.heightProperty());
		box.depthProperty().bind(volume.depthProperty());

		HelperTransform.addTransforms(box, volume.getTransforms());

		return box;
	}

}
