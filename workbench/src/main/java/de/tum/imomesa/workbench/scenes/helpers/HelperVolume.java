package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.volumes.AtomicVolume;
import de.tum.imomesa.model.volumes.BoxVolume;
import de.tum.imomesa.model.volumes.CompositeVolume;
import de.tum.imomesa.model.volumes.CylinderVolume;
import de.tum.imomesa.model.volumes.SphereVolume;
import de.tum.imomesa.model.volumes.Volume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class HelperVolume {

	public static Node paintVolume(List<Element> context, Volume volume, Color color, Configuration config) {
		if (volume == null) {
			return null;
		} else if (volume instanceof AtomicVolume) {
			if (volume instanceof BoxVolume) {
				return HelperBox.addBox(context, (BoxVolume) volume, color, config);
			} else if (volume instanceof CylinderVolume) {
				return HelperCylinder.addCylinder(context, (CylinderVolume) volume, color, config);
			} else if (volume instanceof SphereVolume) {
				return HelperSphere.addSphere(context, (SphereVolume) volume, color, config);
			} else {
				throw new IllegalStateException("Volume type not supported: " + volume.getClass().getName());
			}
		} else if (volume instanceof CompositeVolume) {
			// is composite volume
			CompositeVolume composite = (CompositeVolume) volume;

			Group group = new Group();

			// call method for each child
			for (Volume vPart : composite.getVolumes()) {
				group.getChildren().add(paintVolume(composite.append(context), vPart, color, config));
			}

			HelperTransform.addTransforms(group, composite.getTransforms());

			return group;
		} else {
			throw new IllegalStateException("Volume type not supported: " + volume.getClass().getName());
		}
	}
}
