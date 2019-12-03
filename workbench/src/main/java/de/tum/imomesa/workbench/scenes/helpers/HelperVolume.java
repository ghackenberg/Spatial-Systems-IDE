package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.workbench.scenes.Configuration;
import eu.mihosoft.jcsg.MeshContainer;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class HelperVolume {

	public static Node paintVolume(List<Element> context, Volume volume, PhongMaterial material, Configuration config, boolean selected) {
		if (volume == null) {
			return null;
		} else {
			Group fillGroup = new Group();
			Group lineGroup = new Group();
			
			if (volume.getFirstAncestorByType(Part.class) != null) {
				updateGroup(volume.meshesProperty(), fillGroup, material, DrawMode.FILL, CullFace.NONE);
				updateGroup(volume.meshesProperty(), lineGroup, Color.BLACK, DrawMode.LINE, CullFace.FRONT);
				
				lineGroup.visibleProperty().bind(config.showOutlineProperty());
			} else if (volume.getFirstAncestorByType(Port.class) != null) {
				updateGroup(volume.meshesProperty(), fillGroup, material, DrawMode.FILL, CullFace.NONE);
				updateGroup(volume.meshesProperty(), lineGroup, Color.BLACK, DrawMode.LINE, selected ? CullFace.FRONT : CullFace.BACK);

				fillGroup.setVisible(selected);
			} else {
				throw new IllegalStateException();
			}
			
			Group group = new Group();
			
			group.getChildren().addAll(fillGroup, lineGroup);
			
			return group;
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
	
}
