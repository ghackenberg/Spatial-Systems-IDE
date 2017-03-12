package de.tum.imomesa.workbench.controllers;

import java.io.IOException;
import java.net.URL;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.controllers.main.EditorController;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public abstract class AbstractEditorController {

	@FXML
	private Tab tab;

	private String prefix;

	public AbstractEditorController(String prefix) {
		this.prefix = prefix;
	}

	protected <T> T loadEditor(Element element) {
		FXMLLoader content = null;

		// Process overview elements
		if (element instanceof OverviewElement) {
			// Load overview element
			OverviewElement<?> overview = (OverviewElement<?>) element;
			Element parent = overview.getParent();
			if (parent != null) {
				String name = overview.getName();
				Class<?> iterator = parent.getClass();
				while (Element.class.isAssignableFrom(iterator)) {
					String urlString = prefix + iterator.getSimpleName().toLowerCase() + "/" + name + ".fxml";
					URL url = getClass().getResource(urlString);
					if (url != null) {
						content = loadEditor(url);
						break;
					} else {
						iterator = iterator.getSuperclass();
					}
				}
				if (content == null) {
					element = parent;
				} else {
					element = null;
				}
			}
		}

		// Process normal elements
		while (element != null && content == null) {
			Class<?> iterator = element.getClass();
			while (Element.class.isAssignableFrom(iterator)) {
				String urlString = prefix + iterator.getSimpleName() + ".fxml";
				URL url = EditorController.class.getResource(urlString);
				if (url != null) {
					content = loadEditor(url);
					break;
				} else {
					iterator = iterator.getSuperclass();
				}
			}
			if (content == null) {
				element = element.getParent();
			}
		}

		// Check whether content is available
		if (content != null) {
			tab.setContent(content.getRoot());
			return content.getController();
		} else {
			return null;
		}
	}

	private FXMLLoader loadEditor(URL url) {
		try {
			// create loader
			FXMLLoader fxmlLoader = new FXMLLoader();
			// set location
			fxmlLoader.setLocation(url);
			// load node
			fxmlLoader.load();
			// return fxmlloader
			return fxmlLoader;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
