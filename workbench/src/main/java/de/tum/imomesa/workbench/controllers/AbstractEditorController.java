package de.tum.imomesa.workbench.controllers;

import java.io.IOException;
import java.net.URL;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.workbench.explorers.OverviewElement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public abstract class AbstractEditorController {

	private final String prefix;

	@FXML
	private Tab tab;

	private URL url;
	private Node root;
	private AbstractEditorChildController<Element> controller;

	public AbstractEditorController(String prefix) {
		this.prefix = prefix;
	}

	protected AbstractEditorChildController<Element> loadEditor(final Element original) {
		// Initialize element
		Element element = original;

		// Process overview elements
		if (element instanceof OverviewElement) {
			// Cast element
			OverviewElement<?> overview = (OverviewElement<?>) element;
			// Get parent
			Element parent = overview.getParent();
			// Check parent
			if (parent != null) {
				// Get name
				String name = overview.getName();
				// Get class
				Class<?> iterator = parent.getClass();
				// Iterate class
				while (Element.class.isAssignableFrom(iterator)) {
					// Create url
					String urlString = prefix + iterator.getSimpleName().toLowerCase() + "/" + name + ".fxml";
					// Load url
					if (tryLoading(urlString)) {
						// Check controller
						if (controller != null) {
							// Set root element
							controller.setRootElement(parent);
							// Set selected element
							controller.setSelectedElement(original);
						}
						// Return controller
						return controller;
					} else {
						// Iterate class
						iterator = iterator.getSuperclass();
					}
				}
			}
			// Get parent
			element = element.getParent();
		}

		// Process normal elements
		while (element != null) {
			// Get class
			Class<?> iterator = element.getClass();
			// Iterate class
			while (Element.class.isAssignableFrom(iterator)) {
				// Create url
				String urlString = prefix + iterator.getSimpleName() + ".fxml";
				// Load url
				if (tryLoading(urlString)) {
					// Check controller
					if (controller != null) {
						// Set root element
						controller.setRootElement(element);
						// Set selected element
						controller.setSelectedElement(original);
					}
					// Return controller
					return controller;
				} else {
					// Iterate class
					iterator = iterator.getSuperclass();
				}
			}
			// Iterate element
			element = element.getParent();
		}

		// Return null
		return null;
	}

	private boolean tryLoading(String urlString) {
		// Get resource
		URL url = getClass().getResource(urlString);
		// Check resource
		if (url != null) {
			// Check resource
			if (!url.equals(this.url)) {
				// Load editor
				FXMLLoader loader = createLoader(url);
				// Get root
				root = loader.getRoot();
				// Get controller
				controller = loader.<AbstractEditorChildController<Element>>getController();
				// Set content
				tab.setContent(root);
			}
			// Set url
			this.url = url;
			// Return success
			return true;
		} else {
			// Return failure
			return false;
		}
	}

	private FXMLLoader createLoader(URL url) {
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
