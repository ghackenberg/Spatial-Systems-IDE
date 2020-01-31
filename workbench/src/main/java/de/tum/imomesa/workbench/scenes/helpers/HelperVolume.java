package de.tum.imomesa.workbench.scenes.helpers;

import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.workbench.scenes.Configuration;
import de.tum.imomesa.workbench.scenes.paths.ModelPath;
import de.tum.imomesa.workbench.scenes.paths.ScenePath;
import eu.mihosoft.jcsg.MeshContainer;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperVolume {

	public static void paintVolume(ModelPath<Volume> modelPath, PhongMaterial material, Configuration config, boolean selected, ScenePath scenePath) {
		if (modelPath.element == null) {
			return;
		} else {
			final Group fillGroup = new Group();
			final Group lineGroup = new Group();

			if (modelPath.element.getFirstAncestorByType(Part.class) != null) {
				updateGroup(modelPath.element.meshesProperty(), fillGroup, material, DrawMode.FILL, CullFace.BACK);
				updateGroup(modelPath.element.meshesProperty(), lineGroup, Color.BLACK, DrawMode.LINE, CullFace.FRONT);

				lineGroup.visibleProperty().bind(config.showOutlineProperty());
			} else if (modelPath.element.getFirstAncestorByType(Port.class) != null) {
				updateGroup(modelPath.element.meshesProperty(), fillGroup, material, DrawMode.FILL, CullFace.BACK);
				updateGroup(modelPath.element.meshesProperty(), lineGroup, Color.BLACK, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK);

				fillGroup.setVisible(selected);
			} else {
				throw new IllegalStateException();
			}

			final Group solidOrAlphaChild = new Group();

			solidOrAlphaChild.getChildren().addAll(fillGroup, lineGroup);

			modelPath.element.dirtyProperty().addListener((observable, oldValue, newValue) -> {
				updateParent(modelPath, scenePath, solidOrAlphaChild);
			});
			updateParent(modelPath, scenePath, solidOrAlphaChild);
		}
	}

	private static void updateGroup(ObjectProperty<MeshContainer> container, Group group, Color color, DrawMode mode, CullFace cull) {
		updateGroup(container, group, new PhongMaterial(color), mode, cull);
	}

	private static void updateGroup(ObjectProperty<MeshContainer> container, Group group, PhongMaterial material, DrawMode mode, CullFace cull) {
		container.addListener((property, oldValue, newValue) -> {
			updateGroup(newValue, group, material, mode, cull);
		});
		updateGroup(container.get(), group, material, mode, cull);
	}

	private static void updateGroup(MeshContainer container, Group group, PhongMaterial material, DrawMode mode, CullFace cull) {
		group.getChildren().clear();

		if (container != null) {
			container.getAsMeshViews().forEach(meshView -> {
				group.getChildren().add(meshView);

				meshView.setMaterial(material);
				meshView.setDrawMode(mode);
				meshView.setCullFace(cull);
			});
		}
	}

	private static void updateParent(ModelPath<Volume> modelPath, ScenePath scenePath, Group solidOrAlphaChild) {
		if (modelPath.element.getDirty()) {
			scenePath.alphaParent.getChildren().add(solidOrAlphaChild);
		} else {
			scenePath.solidParent.getChildren().add(solidOrAlphaChild);
		}
	}

}
