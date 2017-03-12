package de.tum.imomesa.workbench.scenes.listeners;

import de.tum.imomesa.model.volumes.AtomicVolume;
import de.tum.imomesa.workbench.scenes.Configuration;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class ColorChangeListener implements ChangeListener<Object> {

	private PhongMaterial material;
	private AtomicVolume volume;
	private Configuration config;
	private boolean selected;

	// constructor
	public ColorChangeListener(PhongMaterial material, AtomicVolume volume, Configuration config, boolean selected) {
		this.material = material;
		this.volume = volume;
		this.config = config;
		this.selected = selected;

		update();
	}

	@Override
	public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
		update();
	}

	private void update() {
		Color color = Color.rgb(volume.getRed(), volume.getGreen(), volume.getBlue());

		material.diffuseColorProperty().bind(Bindings.when(config.highlightSelectedProperty())
				.then(selected ? color : color.darker()).otherwise(color));
	}
}
