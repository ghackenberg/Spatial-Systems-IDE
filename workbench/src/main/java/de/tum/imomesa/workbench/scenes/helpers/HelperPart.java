package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class HelperPart {

	public static Node paintPart(List<Element> context, Part part, Configuration config) {
		
		Group g = new Group();

		if (part.getVolume() != null) {
			
			boolean selected = config.isElementSelected(context, part);

			PhongMaterial material = new PhongMaterial();
			
			part.redProperty().addListener((property, oldValue, newValue) -> {
				updateColor(part, material, config, selected);
			});
			part.greenProperty().addListener((property, oldValue, newValue) -> {
				updateColor(part, material, config, selected);
			});
			part.blueProperty().addListener((property, oldValue, newValue) -> {
				updateColor(part, material, config, selected);
			});
			part.getVolume().dirtyProperty().addListener((property, oldValue, newValue) -> {
				updateColor(part, material, config, selected);
			});
			
			updateColor(part, material, config, selected);
			
			g.getChildren().add(HelperVolume.paintVolume(part.append(context), part.getVolume(), material, config, selected));

			HelperTransform.addTransforms(g, part.getTransforms());
		}

		return g;
	}
	
	private static void updateColor(Part part, PhongMaterial material, Configuration config, boolean selected) {
		// Define color
		Color color = Color.rgb(part.getRed(), part.getGreen(), part.getBlue(), part.getVolume().getDirty() ? 0.5 : 1.0);
		// Bind color
		material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty()).then(selected ? color : color.darker()).otherwise(color));
	}

}
