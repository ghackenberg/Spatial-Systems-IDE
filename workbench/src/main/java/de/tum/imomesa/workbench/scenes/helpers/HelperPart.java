package de.tum.imomesa.workbench.scenes.helpers;

import de.tum.imomesa.model.Part;
import de.tum.imomesa.simulator.Memory;
import de.tum.imomesa.workbench.scenes.Configuration;
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperPart {

	public static void paintPart(ModelPath<Part> modelPath, Configuration config, Memory memory, int step, ScenePath scenePath) {
		final boolean selected = config.isElementSelected(modelPath.path, modelPath.element);
		
		final Group solidChild = new Group();
		final Group alphaChild = new Group();
		
		final ScenePath sceneChild = new ScenePath(scenePath, solidChild, alphaChild);
		
		modelPath.element.volumeProperty().addListener((property, oldValue, newValue) -> {
			updateVolume(modelPath, config, memory, step, sceneChild, selected);
		});
		
		updateVolume(modelPath, config, memory, step, sceneChild, selected);

		HelperTransform.addTransforms(solidChild, modelPath.element.getTransforms());
		HelperTransform.addTransforms(alphaChild, modelPath.element.getTransforms());
	}
	
	private static void updateVolume(ModelPath<Part> modelPath, Configuration config, Memory memory, int step, ScenePath sceneParent, boolean selected) {
		sceneParent.solidParent.getChildren().clear();
		sceneParent.solidParent.getTransforms().clear();
		
		sceneParent.alphaParent.getChildren().clear();
		sceneParent.alphaParent.getTransforms().clear();
		
		if (modelPath.element.getVolume() != null) {

			PhongMaterial material = new PhongMaterial();
			
			modelPath.element.redProperty().addListener((property, oldValue, newValue) -> {
				updateColor(modelPath, config, memory, step, material, selected);
			});
			modelPath.element.greenProperty().addListener((property, oldValue, newValue) -> {
				updateColor(modelPath, config, memory, step, material, selected);
			});
			modelPath.element.blueProperty().addListener((property, oldValue, newValue) -> {
				updateColor(modelPath, config, memory, step, material, selected);
			});
			modelPath.element.getVolume().dirtyProperty().addListener((property, oldValue, newValue) -> {
				updateColor(modelPath, config, memory, step, material, selected);
			});

			updateColor(modelPath, config, memory, step, material, selected);
			
			try {
				if (memory != null && memory.hasDifference(modelPath.path, step) && memory.getDifference(modelPath.path, step) != null) {
					memory.getDifference(modelPath.path, step).toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
						meshView.setMaterial(material);
						meshView.setDrawMode(DrawMode.FILL);
						meshView.setCullFace(CullFace.BACK);
						
						sceneParent.alphaRoot.getChildren().add(meshView);
					});
					
					memory.getDifference(modelPath.path, step).toJavaFXMesh().getAsMeshViews().forEach(meshView -> {
						meshView.setMaterial(new PhongMaterial(Color.BLACK));
						meshView.setDrawMode(DrawMode.LINE);
						meshView.setCullFace(CullFace.FRONT);
						
						sceneParent.alphaRoot.getChildren().add(meshView);
					});
				} else {
					HelperVolume.paintVolume(new ModelPath<>(modelPath, modelPath.element.getVolume()), material, config, selected, sceneParent);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	private static void updateColor(ModelPath<Part> modelPath, Configuration config, Memory memory, int step, PhongMaterial material, boolean selected) {
		try {
			// Detect collision
			boolean collision = (memory != null && memory.getCollisions(modelPath.path, step) != null && memory.getCollisions(modelPath.path, step).size() > 0);
			// Define color
			Color color = Color.rgb(modelPath.element.getRed(), modelPath.element.getGreen(), modelPath.element.getBlue(), (modelPath.element.getVolume().getDirty() || collision) ? 0.5 : 1.0);
			// Bind color
			material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty()).then(selected ? color : color.darker()).otherwise(color));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
