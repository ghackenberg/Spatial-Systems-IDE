package de.tum.imomesa.workbench.scenes.helpers;

import de.tum.imomesa.model.volumes.AtomicVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import de.tum.imomesa.workbench.scenes.listeners.ColorChangeListener;
import de.tum.imomesa.workbench.scenes.managers.ListenerManager;
import javafx.scene.paint.PhongMaterial;

public class HelperColor {

	public static void bindColor(PhongMaterial material, AtomicVolume volume, Configuration config, boolean selected) {
		ColorChangeListener listener = new ColorChangeListener(material, volume, config, selected);

		ListenerManager.getListenerRemover().addListener(volume.redProperty(), listener);
		ListenerManager.getListenerRemover().addListener(volume.greenProperty(), listener);
		ListenerManager.getListenerRemover().addListener(volume.blueProperty(), listener);
	}

}
