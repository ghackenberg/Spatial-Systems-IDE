package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperPart {

	public static Node paintPart(List<Element> context, Part part, Configuration config, Memory memory, int step, Group global) {
		
		final boolean selected = config.isElementSelected(context, part);
		
		final Group local = new Group();
		
		part.volumeProperty().addListener((property, oldValue, newValue) -> {
			updateVolume(context, part, config, memory, step, global, local, selected);
		});
		
		updateVolume(context, part, config, memory, step, global, local, selected);

		HelperTransform.addTransforms(local, part.getTransforms());

		return local;
	}
	
	private static void updateVolume(List<Element> context, Part part, Configuration config, Memory memory, int step, Group global, Group local, boolean selected) {
		local.getChildren().clear();
		local.getTransforms().clear();
		
		if (part.getVolume() != null) {

			PhongMaterial material = new PhongMaterial();
			
			part.redProperty().addListener((property, oldValue, newValue) -> {
				updateColor(context, part, config, memory, step, material, selected);
			});
			part.greenProperty().addListener((property, oldValue, newValue) -> {
				updateColor(context, part, config, memory, step, material, selected);
			});
			part.blueProperty().addListener((property, oldValue, newValue) -> {
				updateColor(context, part, config, memory, step, material, selected);
			});
			part.getVolume().dirtyProperty().addListener((property, oldValue, newValue) -> {
				updateColor(context, part, config, memory, step, material, selected);
			});

			updateColor(context, part, config, memory, step, material, selected);
			
			try {
				if (memory != null && memory.hasDifference(part.append(context), step) && memory.getDifference(part.append(context), step) != null) {
					memory.getDifference(part.append(context), step).toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
						meshView.setMaterial(material);
						meshView.setDrawMode(DrawMode.FILL);
						meshView.setCullFace(CullFace.BACK);
						
						global.getChildren().add(meshView);
					});
					
					memory.getDifference(part.append(context), step).toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
						meshView.setMaterial(new PhongMaterial(Color.BLACK));
						meshView.setDrawMode(DrawMode.LINE);
						meshView.setCullFace(CullFace.FRONT);
						
						global.getChildren().add(meshView);
					});
				} else {
					local.getChildren().add(HelperVolume.paintVolume(part.append(context), part.getVolume(), material, config, selected));
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	private static void updateColor(List<Element> context, Part part, Configuration config, Memory memory, int step, PhongMaterial material, boolean selected) {
		try {
			// Detect collision
			boolean collision = (memory != null && memory.getCollisions(part.append(context), step) != null && memory.getCollisions(part.append(context), step).size() > 0);
			// Define color
			Color color = Color.rgb(part.getRed(), part.getGreen(), part.getBlue(), (part.getVolume().getDirty() || collision) ? 0.5 : 1.0);
			// Bind color
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty()).then(selected ? color : color.darker()).otherwise(color));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
