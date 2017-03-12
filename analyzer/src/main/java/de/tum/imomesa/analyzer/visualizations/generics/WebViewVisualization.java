package de.tum.imomesa.analyzer.visualizations.generics;

import java.io.File;
import java.io.IOException;

import de.tum.imomesa.analyzer.visualizations.Visualization;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebViewVisualization<T> extends Visualization<T, WebView> {

	private File file;

	public WebViewVisualization(T data, File file) {
		super(data);

		this.file = file;
	}

	@Override
	protected WebView generateResult() {
		WebView view = new WebView();
		WebEngine engine = view.getEngine();

		try {
			engine.load(file.toURI().toURL().toExternalForm());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return view;
	}

}
