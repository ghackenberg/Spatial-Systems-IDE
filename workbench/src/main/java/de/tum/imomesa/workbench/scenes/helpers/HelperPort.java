package de.tum.imomesa.workbench.scenes.helpers;

import java.util.List;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.model.ports.EntryLifeMaterialPort;
import de.tum.imomesa.model.ports.ExitLifeMaterialPort;
import de.tum.imomesa.model.ports.InteractionMaterialPort;
import de.tum.imomesa.model.ports.KinematicEnergyPort;
import de.tum.imomesa.model.ports.MaterialPort;
import de.tum.imomesa.model.ports.Port;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class HelperPort {

	public static Node processPort(List<Element> context, Port port, Configuration config) {
		if (port instanceof MaterialPort) {
			return paintPort(context, (MaterialPort) port, config);
		} else if (port instanceof KinematicEnergyPort) {
			return paintPort(context, (KinematicEnergyPort) port, config);
		} else {
			return new Group();
		}
	}

	private static Node paintPort(List<Element> context, MaterialPort port, Configuration config) {
		Group group = new Group();

		Color color = null;

		if (port instanceof InteractionMaterialPort) {
			color = Color.rgb(0, 0, 255); // TODO Use transparent material!
		} else if (port instanceof EntryLifeMaterialPort) {
			color = Color.rgb(0, 255, 0); // TODO Use transparent material!
		} else if (port instanceof ExitLifeMaterialPort) {
			color = Color.rgb(255, 0, 0); // TODO Use transparent material!
		} else {
			throw new IllegalStateException("Port type not supported: " + port.getClass().getName());
		}

		if (port.getVolume() != null) {
			group.getChildren().add(HelperVolume.paintVolume(port.append(context), port.getVolume(), color, config));
		}

		return group;
	}

	private static Node paintPort(List<Element> context, KinematicEnergyPort port, Configuration config) {
		Group group = new Group();

		if (port.getTransform() != null) {
			group.getChildren().add(HelperTransform.paintTransform(port.append(context), port.getTransform(),
					config.selectedElementProperty()));
		}

		return group;
	}
}
