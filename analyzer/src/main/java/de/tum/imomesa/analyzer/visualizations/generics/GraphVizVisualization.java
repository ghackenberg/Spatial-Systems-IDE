package de.tum.imomesa.analyzer.visualizations.generics;

import java.io.File;

import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GraphVizVisualization<T> extends Visualization<T, ImageView> {

	private File file;

	public GraphVizVisualization(T data, File file) {
		super(data);

		this.file = file;
	}

	@Override
	protected ImageView generateResult() {
		try {
			// Rendering
			Runtime.getRuntime().exec("dot -Tpng -o" + file.getName() + ".png " + file.getName()).waitFor();

			// Loading
			ImageView image = new ImageView(new Image(new File(file.getName() + ".png").toURI().toString()));

			// Scroll pane
			return image;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
