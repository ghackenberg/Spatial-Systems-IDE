package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.Part;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.scene.Group;
import javafx.scene.Node;

public class HelperPart {

	public static Node paintPart(List<Element> context, Part part, Configuration config) {
		Group g = new Group();

		if (part.getVolume() != null) {
			g.getChildren().add(HelperVolume.paintVolume(part.append(context), part.getVolume(), null, config));
		}

		HelperTransform.addTransforms(g, part.getTransforms());

		return g;
	}

}
